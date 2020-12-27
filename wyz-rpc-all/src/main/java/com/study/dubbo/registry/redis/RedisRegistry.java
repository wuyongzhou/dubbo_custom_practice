package com.study.dubbo.registry.redis;

import com.study.dubbo.common.tools.URIUtils;
import com.study.dubbo.registry.NotifyListener;
import com.study.dubbo.registry.Registry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class RedisRegistry implements Registry {

    private String prefix="wrpc-";
    //second
    private int beatTimeout=15;
    private URI address;
    private JedisPool jedisPool;

    //存储所有注册服务的URI，便于定期进行心跳设置
    private List<URI> serviceUriList;
    //schedule线程池
    private ThreadPoolTaskScheduler executor;

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

    }

    @Override
    public void init(URI address) {
        this.address=address;
        //redis连接池
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        String host=address.getHost();
        int port = address.getPort();
        String password = URIUtils.getParam(address, "password");
        String timeout = URIUtils.getParam(address, "timeout");
        jedisPool=new JedisPool(jedisPoolConfig, host, port, Integer.parseInt(timeout),password);

        serviceUriList=new ArrayList<>();

        executor=new ThreadPoolTaskScheduler();
        executor.initialize();
        executor.setPoolSize(10);//最多10个并发，默认为1即同步
        executor.setWaitForTasksToCompleteOnShutdown(true);//等待任务完成后关闭
        executor.setAwaitTerminationSeconds(60);//最多等待 60s

        //对已注册的服务进行心跳设置，防止超过预设的存活时间，在当前时间3秒后初次执行，之后每间隔5秒执行一次
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
    }
}
