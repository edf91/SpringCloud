# 目录
[toc]

# 配置详解：
参考网址：
[https://github.com/Netflix/Hystrix/wiki/Configuration](https://github.com/Netflix/Hystrix/wiki/Configuration)

## Hystrix属性4种优先级
  1. 内置全局 
     - 采用ConcurrentHashMap 有 HystrixCommandProperties HystrixThreadPoolProperties HystrixCollapserProperties
  2. 动态全局 
     - HystrixDynamicProperty 
  3. 实例
  4. 动态实例
## Hystrix属性

### Command Properties 相关类 HystrixCommand
#### 1.执行相关 控制HystrixCommand.run() 执行
1. 执行的隔离策略：
    - execution.isolation.strategy
    - 线程池隔离：THREAD
    - 信号量隔离：SEMAPHORE
    - 默认THREAD
    - 信号量适用于接口并发量高的情况，如每秒数百次调用的情况，导致的线程开销过高，通常只适用于非网络调用，执行速度快
2. 超时时间：
    - execution.isolation.thread.timeoutInMilliseconds
    - 默认1000毫秒
    - 超过该时间会执行回退逻辑
3. 是否启用超时设置
    - execution.timeout.enabled
    - 默认true
4. 是否超时中断正在执行的run
    - execution.isolation.thread.interruptOnTimeout
    - 默认true
5. 是否执行取消动作时，中断正在执行的run
    - execution.isolation.thread.interruptOnCancel
    - 默认false
6. 设置最大的信号量，只对于使用信号量[SEMAPHORE]策略的生效
    - execution.isolation.semaphore.maxConcurrentRequests
    - 默认10
    - 如果超出该并发量，则超出的会被拒绝；且该值必需小于容器的线程池大小，否则并不起保护作用，因为其实容器线程池的一小部分而已
                
#### 2. 回退 控制HystrixCommand.getFallback() 执行 对于线程池或者信号量执行策略都生效

1. 最大的并发调用getFallback()
    - fallback.isolation.semaphore.maxConcurrentRequests
    - 默认10
    - 如果超出该数，则后续的会被拒绝，如果没有实现回退逻辑的，则会抛出异常
2. 是否当故障或者拒绝发生后，一个调用尝试调用getFallback()方法
    - fallback.enabled
    - 默认true
    
#### 3. 断路器 控制HystrixCircuitBreaker
1. 是否开启断路器用于健康监控和短路请求
    - circuitBreaker.enabled
    - 默认true
2. 设置一个窗口内的请求数，当在该窗口内(即时间内)请求数达到了该值，则断路器会被打开
    - circuitBreaker.requestVolumeThreshold
    - 默认20
3. 设置在断路打开后，拒绝请求到再次尝试请求并决定断路器是否继续打开的时间
    - circuitBreaker.sleepWindowInMilliseconds
    - 默认5000毫秒
4. 设置打开断路器并走回退逻辑的错误率
    - circuitBreaker.errorThresholdPercentage
    - 默认50%
5. 是否强制打开断路器，如果打开则会拒绝左右的请求
    - circuitBreaker.forceOpen
    - 默认false
    - 优先级比circuitBreaker.forceClosed高
6. 是否强制关闭断路器，则允许所有的请求，无视错误率
    - circuitBreaker.forceClosed
    - 默认false
                
#### 4. 度量 主要度量HystrixCommand 和 HystrixObservableCommand 的执行情况
1. 设置滚动窗口的统计时间
    - metrics.rollingStats.timeInMilliseconds
    - 默认10000毫秒
    - 该项不可以动态修改，以防止统计的不正确
2. 设置滚动的统计窗口被分成的桶的数量
    - metrics.rollingStats.numBuckets
    - 默认10
    - metrics.rollingStats.timeInMilliseconds % metrics.rollingStats.numBuckets == 0 这个必须成立，否则会抛异常
3. 是否开启百分数和均值统计
    - metrics.rollingPercentile.enabled
    - 默认true
    - 如果为false，则值为-1
4. 设置滚动窗口的持续时间，其中执行时间保持在百分位数中
    - metrics.rollingPercentile.timeInMilliseconds
    - 默认60000
5. numBuckets
    - metrics.rollingPercentile.numBuckets
    - 默认6
6. bucketSize
    - metrics.rollingPercentile.bucketSize
    - 默认100
7. intervalInMilliseconds
    - metrics.healthSnapshot.intervalInMilliseconds
    - 默认500
                
#### 5. 请求上下文 控制HystrixRequestContext 被HystrixCommand使用
1. 是否启动当HystrixCommand.getCacheKey()调用后，缓存到HystrixRequestCache
    - requestCache.enabled
    - 默认true
2. 是否记录HystrixCommand执行或者事件的日志到HystrixRequestLog
    - requestLog.enabled
    - 默认true
           
### Collapser Properties 控制HystrixCollapser
1. 设置在批处理请求中，允许的最大请求数
    - maxRequestsInBatch
    - 默认Integer.MAX_VALUE
2. 设置批处理在多少毫秒后出发执行
    - timerDelayInMilliseconds
    - 默认10毫秒
3. 是否缓存HystrixCollapser.execute() 和 HystrixCollapser.queue() 
    - requestCache.enabled
    - 默认true
        
### Thread Pool Properties 控制执行的线程池
1. 执行线程数
    - coreSize
    - 默认10
2. 最大执行线程数 通常同1一样大小
    - maximumSize
    - 默认10
3. 设置使用哪种BlockingQueue，如果-1为SynchronousQueue；其他则为LinkedBlockingQueue
    - maxQueueSize
    - 默认-1
    - 不支持动态调整
4. 设置拒绝队列大小，这个属性是因为maxQueueSize无法动态改变，但需要去动态改变队列大小
    - queueSizeRejectionThreshold
    - 默认5
    - 当maxQueueSize为-1，则该属性不可用
5. 设置线程存活多少毫秒
    - keepAliveTimeMinutes
    - 默认1
6. 设置maximumSize启作用
    - allowMaximumSizeToDivergeFromCoreSize
    - 默认false
7. 滚动窗口
    - metrics.rollingStats.timeInMilliseconds
    - 默认10000毫秒
8. 设置桶数 必需metrics.rollingStats.timeInMilliseconds % metrics.rollingStats.numBuckets == 0 否则抛异常
    - metrics.rollingStats.numBuckets
    - 默认10