package com.study.dubbo.registry.redis;

import com.study.dubbo.common.tools.URIUtils;
import com.study.dubbo.registry.NotifyListener;
import com.study.dubbo.registry.Registry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RedisRegistry implements Registry {

    private String prefix="wrpc-";
    //second
    private int beatTimeout=15;
    private JedisPool jedisPool;
    //schedule线程池
    private ThreadPoolTaskScheduler executor;

    //提供者 --- 存储所有注册服务的URI，便于定期进行心跳设置
    private List<URI> serviceUriList;

    //消费者
    private JedisPubSub jedisPubSub;
    private Map<String, Set<URI>> localCache;
    private Map<String,NotifyListener>listenerMap;


    @Override
    public void registerService(URI exportUri) {
        Jedis jedis=null;
        try {
            jedis = jedisPool.getResource();
            jedis.setex(prefix+exportUri.toString(),beatTimeout,String.valueOf(System.currentTimeMillis()));
            //加入集合中，用于定期心跳设置
            serviceUriList.add(exportUri);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    @Override
    public void subscribeService(String serviceName, NotifyListener notifyListener) {
        Jedis jedis=null;
        try {
            //1.本地缓存，当该服务第一次被订阅时才会执行
            if(localCache.get(serviceName)==null){
                localCache.putIfAbsent(serviceName,new HashSet<>());
                listenerMap.putIfAbsent(serviceName,notifyListener);
                //2.模糊获取redis中指定服务的uri信息
                jedis = jedisPool.getResource();
                // wrpc-WrpcProtocol://127.0.0.1:10088/com.study.dubbo.sms.api.SmsService?transporter=Netty4Transporter&serialization=JsonSerialization
                Set<String> serviceInstances = jedis.keys(prefix+"*" + serviceName + "?*");
                //3.遍历每个服务实例信息，加入到该服务对应的value中
                for(String instances:serviceInstances){
                    URI instanceUri=new URI(instances.replace(prefix, ""));
                    localCache.get(serviceName).add(instanceUri);
                }
                //4.最后通知发起订阅的调用者，当前共有多少个服务实例
                notifyListener.notify(localCache.get(serviceName));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
    }

    @Override
    public void init(URI address) {
        //redis连接池
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        String host=address.getHost();
        int port = address.getPort();
        String password = URIUtils.getParam(address, "password");
        String timeout = URIUtils.getParam(address, "timeout");
        jedisPool=new JedisPool(jedisPoolConfig, host, port, Integer.parseInt(timeout),password);

        serviceUriList=new ArrayList<>();
        localCache=new ConcurrentHashMap<>();
        listenerMap=new ConcurrentHashMap<>();

        executor=new ThreadPoolTaskScheduler();
        executor.initialize();
        executor.setPoolSize(10);//最多10个并发，默认为1即同步
        executor.setWaitForTasksToCompleteOnShutdown(true);//等待任务完成后关闭
        executor.setAwaitTerminationSeconds(60);//最多等待 60s

        /**
         * 服务提供者关注的定时任务
         * 对已注册的服务进行心跳设置，防止超过预设的存活时间，在当前时间3秒后初次执行，之后每间隔5秒执行一次
         */
        executor.scheduleWithFixedDelay(()->{
            Jedis jedis=null;
            try {
                jedis = jedisPool.getResource();
                //协议名称://IP:端口/service全类名?参数名称=参数值&参数1名称=参数2值
                for (URI uri:serviceUriList){
                    jedis.expire(prefix+uri.toString(),beatTimeout);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(jedis!=null){
                    jedis.close();
                }
            }
        }, Instant.ofEpochMilli(System.currentTimeMillis() + 3000), Duration.ofSeconds(5));

        /**
         * 服务消费者关心的定时任务
         * 监听服务变动，需要手动修改配置文件开启 - redis.conf  notify-keyspace-events KE$xg
         * 根据配置，onPMessage的message会返回不同事件的通知
         */
        executor.execute(()->{
            Jedis jedis=null;
            try {
                //事件具体执行类
                jedisPubSub = new JedisPubSub() {
                    @Override
                    public void onPSubscribe(String pattern, int subscribedChannels) {
                        System.out.println("注册中心开始监听:" + pattern);
                    }

                    /**
                     * 事件回调方法
                     * @param pattern
                     * @param channel
                     * @param message 事件的具体类型
                     */
                    @Override
                    public void onPMessage(String pattern, String channel, String message) {
                        try {
                            //replace后：WrpcProtocol://127.0.0.1:10088/com.study.dubbo.sms.api.SmsService?transporter=Netty4Transporter&serialization=JsonSerialization
                            URI serviceURI = new URI(channel.replace("__keyspace@0__:"+prefix, ""));
                            if ("set".equals(message)) {
                                // 新增
                                Set<URI> uris = localCache.get(URIUtils.getService(serviceURI));
                                if (uris != null) {
                                    uris.add(serviceURI);
                                }
                            }
                            if ("expired".equals(message)) {
                                // 过期
                                Set<URI> uris = localCache.get(URIUtils.getService(serviceURI));
                                if (uris != null) {
                                    uris.remove(serviceURI);
                                }
                            }
                            if ("set".equals(message) || "expired".equals(message)) {
                                System.out.println("服务实例有变化，开始刷新---------");
                                NotifyListener notifyListener = listenerMap.get(URIUtils.getService(serviceURI));
                                if (notifyListener != null) {
                                    notifyListener.notify(localCache.get(URIUtils.getService(serviceURI)));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                jedis = jedisPool.getResource();
                //监听redis中【wrpc-*】的事件通知
                jedis.psubscribe(jedisPubSub, "__keyspace@0__:"+prefix+"*");
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(jedis!=null){
                    jedis.close();
                }
            }
        });
    }
}
