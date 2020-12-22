**从零打造一个自己的RPC框架** - **Tony-RPC (TRPC)**



1. 构思整体思路
   1. 注解编写 -- @service **@reference**
   2. 服务提供者 - 启动时扫描service -->  启动网络服务，接受请求 --> 将端口、Ip、服务信息保存到注册中心
   3. 服务消费者 - 启动时注入reference --> 生成接口代理 --> 调用时发起网络请求 
   4. 编写框架的前提知识【网络编程、反射、代理、SPI、多线程】
      * SPI机制说明 -- java机制 -- 拓展API   第三方 - 拓展
   5. 面向抽象、接口编程，在设计时尽可能的考虑扩展多样性，例如既可以选择Netty作为网络框架，也可以选择Mina。
      在协议方面可以选择Http也可以RPC，通过不指定具体细节实现可以提供更好扩展。
      * 例如在该示例代码中，就是结合SPI机制根据配置文件指定具体的实现，来组合成一个基本的网络服务应用。
      * 各自负责自身模块的职责功能
 
2.  服务提供者

   * 借助Spring完成 **服务暴露** 注解的扫描 - bean生命周期

   * 基于Netty编写网络服务端程序 - 最基础网络编程

     * remoting 包 网络底层 【端口、IP、协议】

     * 1. 配置文件来的 - spring 拓展 bean定义 配置对象的创建

     * 2. 创建 Transporter 统一接口 - 创建服务器、连接远程接口 唯一入口

       3. netty实现 - handler

       4. 服务开放【无任何意义】

          【**非常之重要** - 照着写 - Dubbo、SpringCloud 十年 底层知识 】

   * 接口调用**基本的交互设计**【Response响应、RpcInvocation调用请求】

     *  收到消费者请求 -- 调用远程方法
     * 客户端 --> 说明  service名称 方法名 方法参数 方法值  --> RpcInvocation
     * 服务器响应 --> Response

   * trpcProtocol协议设计【tcp -- 长连接】【应用层协议】

     * 具体传输内容【协议头(标记、业务body长度)】【业务body (RpcInvocation/Response)】
     * TRPCProtocol：
     *  0xdabb（两字节） + body长度(4字节) + body（默认json序列化）
     * 头 6字节 + body 长度 N （处理粘包、拆包的现象）
     *  netty**责任链** -- 一个数据到了netty 一步步处理。 由第一个 **处理器** 传到 最后一个
       * 数据 -- 向后传递 -- 处理器决定 --- 

   * trpcProtocol协议具体实现

     * rpc -- protocol包 -- 完成具体协议定义
     * 缓存  不能处理
     * 粘包 - 一次数据读取 到 多个 请求包
     * 拆包 -  读取不足一个请求的数据、处理完之后，剩余不足一个请求的数据
     * 【**测试**】 -- 发出正确的协议请求
     * 通过测试客户端，掌握整个协议。
     * netty 线程安全 -- 每个连接 都能独立handler、codec
     * 收到请求。如何完成方法调用 -- 框架相集成

   * 服务实现者Invoker设计【代理调用机制】

     *  收到 -- 调用方法 --- 集成 
     * common 配置 工具类、 config 集成注解、配置对象、remoting 纯网络底层模块、rpc 协议
     * 调用对象 -- @TRpcService 注解标记的对象 ---  
     * invoker 传递  怎么传递... serviceConfig bootstrap
     * 【测试】 - 服务提供者代码测试

   * 响应功能

     *  编码 -- channel发送 write 触发 -- 构建完整的协议报文

   * 服务注册模块设计

     * redis -- 存储 【拓展】
     * 什么时候调用

3.  **服务消费者**【单机调用】

   * 借助Spring完成 **服务引用** 注解的扫描

     * 没有实现类 -- 引用 代理对象
     *  Invoker
     * 创建**动态**代理Proxy对象

     * 代理对象 创建出来 用于注入
     * 实际 构建 rpcInvocation参数
     *  本质调用 Invoker -- 客户端 --- 调用远程服务提供者一个对象
     * 基于Netty编写网络客户端程序

     * 长连接 --- remoting包 
     * Client 客户端
     * 统一入口 Transporter

   * 服务消费者 Invoker设计【单机调用】

     * 代理---> invoker--> 根据不同的协议 服务提供者地址 ---> 发
     * 长连接机制与 --- 多线程调用机制设计 future机制完成分发
     * remoting 调用统一入口 -- Transporter
     * Protocol 对于上层代码 ：统一入口 -- 导出服务、消费者获取invoker（底层网络交互）
     * 服务接口引用 -- 注入--proxy--invoker--> rpcinvocation-->invoker-->服务实现者

   * 集群模式下的调用设计 -- 多个服务实例

     * 服务发现机制设计与编写 -- 代理 --- invoker【一个服务实例】 -- clusterInvoker
     * 客户端负载均衡器设计与编写
       * 随机 -- 最简单的计算
       * 轮询 【更新负载均衡策略，不是简单计算。记录调用的情况】



层次

common 序列化、工具包

config 配置 注解、spring、bootstrap

registry 注册机制

remoting 网络 独立 -- 底层数据传输  

protocol 协议 -- 导出、invoker

proxy ： 消费调用服务 invoker  服务调用实现类 invoker

cluster： 集群相关：负载均衡、重试、容错 都可以拓展

