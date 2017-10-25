[toc]
# SpringCloud
## 环境信息
- eureka-server 
    - 2台 30000  30001
- u-service
    - 2台  30002 30003
- u-consumer
    - 2台  30004 30005
- zipkin
    - 1台 30006 
- hystrix-dashboard
    - 1台 30008
- turbine
    - 1台 30010
- zuul
    - 2台 30012 30013
- sidecar
    - 2台 30014 30015
- dubbo
    - 2台 30016 30017

- 注意：hystrix、ribbon、zuul等即代理，遇到文件上传，需要主要timeout设置

## eureka
- 实现cap中的ap，每个客户端都有一份副本，这样不会在server挂了不可用；
- 默认90秒没收到服务的心跳信息则会注销该服务，提供自我保护模式，防止网络异常导致注销大量服务；
- client默认30秒向server发送心跳；
- client会缓存服务注册表中的信息，可以提高性能，降低server压力，高可用；
- 主要配置：
  - eureka.client.registerWithEureka是否将自己注册到server，默认true
  - eureka.client.fetchRegistry是否从server获取信息，默认true
  - eureka.client.serviceUrl.defaultZone设置server地址
  - eureka.client.healthcheck.enabled是否开启健康检查，即调用/health来确定是否服务可用，而不只通过心跳来检测
  - eureka.instance.leaseRenewalIntervalInSeconds设置服务注册心跳秒数，默认30秒
- 配合security增加用户认证
- 提供REST端点，集成其他非JAVA微服务
- 提供多网卡环境配置
- eureka说明地址： [https://github.com/wangxiaodong91/SpringCloud/blob/master/eureka-server/README.md](https://github.com/wangxiaodong91/SpringCloud/blob/master/eureka-server/README.md)
    
## ribbon
- 负载均衡 配合 eureka 实现通过虚拟机主名称调用服务
- 虚拟主机名称不能有"_"，否则调用时会报错
- 自定义ribbon属性 clientName.ribbon.[ILoadBalancer|IRule|IPing|ServerList|ServerListFilter]实现类
- 支持其他非eureka支持的 实现负载均衡，只需配置clientName.ribbon.listOfServers:[host:port,host2:port2]


## hystrix
- 熔断，基于archaius实现动态配置
- 通过/health基于查看断路器是否打开，默认是5秒内20次失败，则会打开
- dashboard监控单个，turbine聚合多个
- turbine可以配置mq进行收集信息，客户端需要引入mq，修改注解为@EnableTurbineStream，并移除配置，采用mq配置即可
- 属性说明地址： [https://github.com/wangxiaodong91/SpringCloud/blob/master/hystrix-dashboard/README.md](https://github.com/wangxiaodong91/SpringCloud/blob/master/hystrix-dashboard/README.md)

## sleuth
- 链路监控
  - span(跨度)：基本工作单元，初始化span被称为"root span"
  - trace（跟踪）：一组共享"root span"的span组成的树状结构称为trace
  - annotation（标注）：用来记录事件的存在，核心annotation用来定义请求的开始和结束
    - CS：客户端发送一个请求
    - SR：服务端接收请求并准备处理它，如果SR-CS则为网络延迟
    - SS：服务端发送，表示完成请求处理，SS-SR则为服务端处理请求所需时间
    - CR：客户端接收，span结束的标记，CR-CS则表示客户端发送请求到接收到响应所需时间
  - 同ELK整合日志输出
  - 整合Zipkin，可视化查看追踪日志信息
    
## zipkin
 - 链路监控收集 配合 sleuth使用
 - 可以指定采样的请求百分比，默认0.1，即10%
    
## feign
 - 声明式REST调用 整合了 eureka、ribbon和hystrix
 - 支持请求或响应压缩
 - hytrix支持
   - 熔断时，可以通过FallbackFactory检查回退的原因
   - 如果要监控，需要单独引入hystrix并开启EnableCircuitBreaker注解
   - 配置feign.hystrix.enabled是否禁用熔断，全局 也可以通过configuration指定配置类，局部禁用熔断

## zuul
 - 默认整合了Eureka、Ribbon、Hystrix等，可以实现如下功能：
   - 身份认证与安全
   - 审查与监控
   - 动态路由
   - 压力测试
   - 负载分配
   - 静态响应处理
   - 多区域弹性
 - 如果依赖了spring security，则默认会忽略：Pragma、Cache-Control、X-Frame-Options、X-Content-Type-Options、X-XSS-Protection、Expires等头信息。
 - 如果代理文件上传，需要注意上传大小、超时时间等
 - 可以自定义实现熔断、回退。
 - 可以实现请求的聚合，但一般不会这么做，耦合服务
 - 默认使用Apache的HTTPClient发起http请求，可以使用RestClient或者OkHttpClient，通过配置ribbon.[restclient|okhttp].enabled=true进行切换
 - /routes 端点可以管理zuul路由 POST会强制刷新路由，GET返回列表 [注：需要设置 management.security.enabled=false]
 - 路由配置支持，指定服务，正则，忽略指定服务，增加前缀等；也可以自定义实现；header可控
 - 过滤器：PRE（请求前）、ROUTING（将请求路由到服务，用于构建发给服务的请求，可以使用httpClient或ribbon）、POST（可以添加响应header等）、ERROR（发生错误时执行）和自定义过滤器
 - 禁用过滤器：zuul.[过滤器类名].[过滤器类型].disabled=true即可，zuul默认有：DebugFilter、FormBodyWarpperFilter、PreDecorationFilter等过滤器位于filters包下

## sidecar
 - 整合了zuul 
 - 用于整合非eureka的微服务，即通过sidecar进行转发，多了一层sidecar的转发，如dubbo
 - sidecar 会获取非eureka微服务的健康状态，并传播到eureka
 - 消费方要使用dubbo服务，通过Ribbon或者Feign进行sidecarServiceId/dubboApi调用
 - /hosts/serviceId 获取sidecar代理的实例 实际上整合也就是通过该接口获取到被整合的服务的地址和端口，再进行转发
 - /routes 可以看到被代理的springCloud服务
 - 非springCloud需要实现/health接口，并返回 {status:"UP"}的json文档，让sidecar判断服务是否可用。
 
  
  
  
## config
 - 统一配置管理
 - 基于端点发送refresh手动刷新
 - 基于Bus[依赖mq]实现自动刷新，轻量级消息代理

## 源码地址
[https://github.com/wangxiaodong91/SpringCloud](https://github.com/wangxiaodong91/SpringCloud)