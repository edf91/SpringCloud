# 目录
[toc]
# SpringCloud-Eureka

## 整合项目

[https://github.com/spring-cloud/spring-cloud-netflix](https://github.com/spring-cloud/spring-cloud-netflix)

spring-cloud-netflix-eureka-server

spring-cloud-netflix-eureka-client


[SpringCloud 集成Netflix的组件说明文档地址](http://cloud.spring.io/spring-cloud-netflix/single/spring-cloud-netflix.html)

Netflix的Wiki[https://github.com/Netflix/eureka/wiki](https://github.com/Netflix/eureka/wiki)

### Eureka架构图

![image](https://github.com/Netflix/eureka/raw/master/images/eureka_architecture.png)
#### 关键概念

##### Region(区域)
   - 属于AWS概念，主要为了给基础设施服务划分区域，实现高可用，提高网络传输速度。
##### Zone(可用区)
   - 属于AWS概念，为了实现高可用，在每个Region中可以有多个Zone，且每个Zone都是独立的，一个可用区出问题不会影响到其他可用区，这样也可以实现网络的低延迟。上图中的us-east-1c表示的就是一个可用区。
##### 租约(lease)
   - Eureka Server 用于管理Erueka Client(主要管理Application Service)
   - 客户端通过每隔30秒，向Eureka Server发送心跳来续约，如果Eureka Server在90秒内没有收到客户端的续约，则会将该客户端从注册表里删除。
##### Eureka Server
   - 提供服务的注册和发现的功能
   - Register 提供服务注册功能
   - Renew 提供服务续租约(lease)功能
   - Cancel 提供服务注销功能
   - Get Registry 提供注册表获取功能，即服务发现
##### Application Service
   - 服务提供者。
##### Application Client
   - 服务消费者，每个client会缓存注册表的信息，这样可以再Eureka Server不可用的时候，不影响服务消费者同服务提供者的交互，同ZK的主要区别，即实现CAP中的AP。

## SpringCloud启动Eureka 的过程：

### EnableEurekaServer注解
SpringCloud通过注解**EnableEurekaServer**启动eureka服务，其包含了EnableDiscoveryClient。

### SpringCloud与jersey Rest框架
eureka 基于**jersey**实现Rest服务，因此，如果**不想采用jersey**，则只需要过滤相关包的依赖即可，SpringCLoud则会采用RestTemplate来发送Rest请求。这也说明了eureka其是基于**Servlet**实现的。

### jersey启动
SpringCloud在容器启动的时候，动态添加过滤器**servletContainer** 并拦截/eureka/* 的url。在该过滤器初始化的时候便加载了**com.sun.jersey.spi.container.servlet.ServletContainer** 该filter包含的classes有：作为jersey的**WebComponent**的**ResourceConfig**
- com.netflix.eureka.resources.ASGResource
- com.netflix.discovery.provider.DiscoveryJerseyProvider
- com.netflix.eureka.resources.ApplicationsResource
- com.netflix.eureka.resources.StatusResource
- com.netflix.eureka.resources.PeerReplicationResource
- com.netflix.eureka.resources.VIPResource
- com.netflix.eureka.resources.ServerInfoResource
- com.netflix.eureka.resources.InstancesResource
- com.netflix.eureka.resources.SecureVIPResource

再通过**WebApplicationProvider**初始化jersey服务。具体实现为**WebApplicationImpl._initiate**方法。

**WebApplicationFactory**
```java
public static WebApplication createWebApplication() throws ContainerException {
    for (WebApplicationProvider wap : ServiceFinder.find(WebApplicationProvider.class)) {
        // Use the first provider found
        // 创建jersey服务
        return wap.createWebApplication();
    }
    throw new ContainerException("No WebApplication provider is present");
}
```

再由**com.sun.jersey.core.spi.component.ProviderFactory** 通过反射实例化**com.netflix.discovery.provider.DiscoveryJerseyProvider**负责将对象实例化和反序列化发送到eureka服务器
```java
    private ComponentProvider __getComponentProvider(Class c) {
        try {
            ComponentInjector ci = new ComponentInjector(this.ipc, c);
            ComponentConstructor cc = new ComponentConstructor(this.ipc, c, ci);
            // 实例化Provider
            Object o = cc.getInstance();
            return new ProviderFactory.SingletonComponentProvider(ci, o);
        } catch (NoClassDefFoundError var5) {
            LOGGER.log(Level.CONFIG, "A dependent class, " + var5.getLocalizedMessage() + ", of the component " + c + " is not found." + " The component is ignored.");
            return null;
        } catch (InvocationTargetException var6) {
            if(var6.getCause() instanceof NoClassDefFoundError) {
                NoClassDefFoundError ncdf = (NoClassDefFoundError)var6.getCause();
                LOGGER.log(Level.CONFIG, "A dependent class, " + ncdf.getLocalizedMessage() + ", of the component " + c + " is not found." + " The component is ignored.");
                return null;
            } else {
                LOGGER.log(Level.SEVERE, "The provider class, " + c + ", could not be instantiated. Processing will continue but the class will not be utilized", var6.getTargetException());
                return null;
            }
        } catch (Exception var7) {
            LOGGER.log(Level.SEVERE, "The provider class, " + c + ", could not be instantiated. Processing will continue but the class will not be utilized", var7);
            return null;
        }
    }
```

### 配置信息加载
**ConfigurationClassEnhancer**
  - 负责注解Configuration和注解Bean等的实例化，如：
    - **WebMvcConfigurationSupport**根据classpath中是否存在gson、jackson等来
    - **ArchaiusAutoConfiguration**加载archaius配置信息
```java
protected void configureArchaius(ConfigurableEnvironmentConfiguration envConfig) {
	if (initialized.compareAndSet(false, true)) {
	    // 获取appName 没有配置则用默认
		String appName = this.env.getProperty("spring.application.name");
		if (appName == null) {
			appName = "application";
			log.warn("No spring.application.name found, defaulting to 'application'");
		}
	    // 后面代码省略...	
	}
}    
```

**DefaultListableBeanFactory**基于代理实例化eureka组件等，如配置信息，EurekaClient

### EurekaClientAutoConfiguration InstanceInfo EurekaClient

#### EurekaClientConfigBean
    SpringCloud对EurekaClient的配置项
**EurekaClientAutoConfiguration**设置向eureka server或者向其他服务发现组件 注册需要的信息即**InstanceInfo**，此处为SpringCloud做的适配。

```java
@ProvidedBy(EurekaConfigBasedInstanceInfoProvider.class)
@Serializer("com.netflix.discovery.converters.EntityBodyConverter")
@XStreamAlias("instance")// xml格式的节点
@JsonRootName("instance")// json格式的节点
public class InstanceInfo {//代码省略...}

@Bean
@ConditionalOnMissingBean(value = ApplicationInfoManager.class, search = SearchStrategy.CURRENT)
@org.springframework.cloud.context.config.annotation.RefreshScope
@Lazy
public ApplicationInfoManager eurekaApplicationInfoManager(EurekaInstanceConfig config) {
    // 实例化向注册表注册所需要的信息，如eureka主页地址、本机ip、appName等
    InstanceInfo instanceInfo = new InstanceInfoFactory().create(config);
    return new ApplicationInfoManager(config, instanceInfo);
}
```
**EurekaClientAutoConfiguration**实例化**EurekaClient**，设置配置信息，**PropertyBasedClientConfigConstants**为配置变量名，以及一些默认值，实现类为**DiscoveryClient** 
- EurekaClientAutoConfiguration 源码
```java

@Bean(destroyMethod = "shutdown")
@ConditionalOnMissingBean(value = EurekaClient.class, search = SearchStrategy.CURRENT)
@org.springframework.cloud.context.config.annotation.RefreshScope
@Lazy
public EurekaClient eurekaClient(ApplicationInfoManager manager, EurekaClientConfig config, EurekaInstanceConfig instance) {
    manager.getInfo(); // force initialization
    return new CloudEurekaClient(manager, config, this.optionalArgs,
					this.context);
}
```
也会通过EurekaAutoServiceRegistration，将服务自动注册到SpringCloud的服务发现注册框架，定时进行健康检查。
```java
@Bean
@ConditionalOnBean(AutoServiceRegistrationProperties.class)
@ConditionalOnProperty(value = "spring.cloud.service-registry.auto-registration.enabled", matchIfMissing = true)
public EurekaAutoServiceRegistration eurekaAutoServiceRegistration(ApplicationContext context, EurekaServiceRegistry registry, EurekaRegistration registration) {
    return new EurekaAutoServiceRegistration(context, registry, registration);
}
```

#### DiscoveryClient 源码
- DiscoveryClient 源码
  - 继承关系
    
    **DiscoveryClient** 实现了 **EurekaClient**，EurekaClient继承了**LookupService**。
    ![image](http://note.youdao.com/yws/public/resource/474a64dcff40ff49b32f44effeab6b7d/xmlnote/WEBRESOURCE3938b47715f8e50cabbb1daf5e706cbc/537)
    
    **DiscoveryClient**主要负责与eureka server交互，需要配置servers的url，支持故障转移。eureka client 的主要作用有：
       - 注册实力到eureka server
       - 向eureka server 续租约
       - 在cleint关闭时，取消同eureka server的租约
       - 查询eureka server中的注册信息
    
    **EurekaClient**定义一个简单的接口，给**DiscoveryClient**实现,主要为了兼容eureka 1.x版本，使得1.x版本更容易过渡到2.x版本，主要作用有：
       - 提供各种不同的方式，以获取各种InstanceInfos的能力
       - 提供获取客户端数据的能力，如获取regions等。
       - 提供注册和访问健康检查的能力
    
    <div id="LookupService"></div>

    **LookupService**提供查询所有活动的Instances的接口。 

```java
@Inject
DiscoveryClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig config, AbstractDiscoveryClientOptionalArgs args,
                Provider<BackupRegistry> backupRegistryProvider) {
    if (args != null) {
        this.healthCheckHandlerProvider = args.healthCheckHandlerProvider;
        this.healthCheckCallbackProvider = args.healthCheckCallbackProvider;
        this.eventListeners.addAll(args.getEventListeners());
    } else {
        this.healthCheckCallbackProvider = null;
        this.healthCheckHandlerProvider = null;
    }
    // 向注册表注册所需的信息，提供了各种注册组件需要的实现，也可以自己自定义实现，如
    // AbstractInstanceConfig提供了大量的默认信息；
    // MyDataCenterInstanceConfigProvider提供非aws的数据中心；
    // CloudInstanceConfigProvider提供aws注册所需的;
    // EurekaInstanceConfig提供了向eureka注册所需的；
    // ApplicationInfoManager 使用的是EurekaInstanceConfig
    this.applicationInfoManager = applicationInfoManager;
    InstanceInfo myInfo = applicationInfoManager.getInfo();
    // 客户端配置 大部分信息采用DefaultEurekaClientConfig
    clientConfig = config;
    // 已经过时，主要为了兼容遗留客户端问题
    staticClientConfig = clientConfig;
    // 传输层如http请求超时、重试等信息
    transportConfig = config.getTransportConfig();
    // 该eureka实例的信息 如主机信息，健康检测接口等
    instanceInfo = myInfo;
    if (myInfo != null) {
        // 服务唯一地址  如：EUREKA-SERVER/172.16.17.60:eureka-server:30000
        appPathIdentifier = instanceInfo.getAppName() + "/" + instanceInfo.getId();
    } else {
        logger.warn("Setting instanceInfo to a passed in null value");
    }
    // 备份注册表信息，当服务端不可用时，客户度可以从这里获取注册表信息
    this.backupRegistryProvider = backupRegistryProvider;
    // 如果eureka server的地址来源dns服务，则随机获取urls
    this.urlRandomizer = new EndpointUtils.InstanceInfoBasedUrlRandomizer(instanceInfo);
    // 采用cas Applications存放的时服务器返回的存储客户端注册信息的
    localRegionApps.set(new Applications());
    // cas 递增版本，防止客户端注册旧的信息
    fetchRegistryGeneration = new AtomicLong(0);
    
    remoteRegionsToFetch = new AtomicReference<String>(clientConfig.fetchRegistryForRemoteRegions());
    remoteRegionsRef = new AtomicReference<>(remoteRegionsToFetch.get() == null ? null : remoteRegionsToFetch.get().split(","));
    // 判断是否需要从eureka server 获取注册表信息 并初始化相应的度量信息
    if (config.shouldFetchRegistry()) {
        this.registryStalenessMonitor = new ThresholdLevelsMetric(this, METRIC_REGISTRY_PREFIX + "lastUpdateSec_", new long[]{15L, 30L, 60L, 120L, 240L, 480L});
    } else {
        this.registryStalenessMonitor = ThresholdLevelsMetric.NO_OP_METRIC;
    }
    // 是否需要将信息注册到eureka server上，通过这个开关可以实现,
    // 只获取其他实例的信息，而不将自己的信息给其他客户端发现
    if (config.shouldRegisterWithEureka()) {
        this.heartbeatStalenessMonitor = new ThresholdLevelsMetric(this, METRIC_REGISTRATION_PREFIX + "lastHeartbeatSec_", new long[]{15L, 30L, 60L, 120L, 240L, 480L});
    } else {
        this.heartbeatStalenessMonitor = ThresholdLevelsMetric.NO_OP_METRIC;
    }
    
    // 属于aws的基础概念regin和zone 默认值为us-east-1
    logger.info("Initializing Eureka in region {}", clientConfig.getRegion());
    
    // 如果不需要注册信息到server和拉取注册信息表，则初始化完成。
    if (!config.shouldRegisterWithEureka() && !config.shouldFetchRegistry()) {
        logger.info("Client configured to neither register nor query for data.");
        scheduler = null;
        heartbeatExecutor = null;
        cacheRefreshExecutor = null;
        eurekaTransport = null;
        instanceRegionChecker = new InstanceRegionChecker(new PropertyBasedAzToRegionMapper(config), clientConfig.getRegion());

        // This is a bit of hack to allow for existing code using DiscoveryManager.getInstance()
        // to work with DI'd DiscoveryClient
        DiscoveryManager.getInstance().setDiscoveryClient(this);
        DiscoveryManager.getInstance().setEurekaClientConfig(config);

        initTimestampMs = System.currentTimeMillis();

        logger.info("Discovery Client initialized at timestamp {} with initial instances count: {}",
                initTimestampMs, this.getApplications().size());
        return;  // no need to setup up an network tasks and we are done
    }

    try {
        // 初始化调度线程池 3个核心线程 并且为后台运行。主要负责：
        // server 的url更新
        // 调度TimedSuperVisorTask被该TimerTask所包裹的线程必须是线程安全的，负责在子任务超时时，强制子任务超时。
        scheduler = Executors.newScheduledThreadPool(3,
                new ThreadFactoryBuilder()
                        .setNameFormat("DiscoveryClient-%d")
                        .setDaemon(true)
                        .build());
        // 实例化心跳线程池，1个核心线程，默认最大的线程数为5个，使用直接提交线程
        heartbeatExecutor = new ThreadPoolExecutor(
                1, clientConfig.getHeartbeatExecutorThreadPoolSize(), 0, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadFactoryBuilder()
                        .setNameFormat("DiscoveryClient-HeartbeatExecutor-%d")
                        .setDaemon(true)
                        .build()
        );  // use direct handoff
        // 实例化注册表缓存刷新线程池，最大线程数默认5个
        cacheRefreshExecutor = new ThreadPoolExecutor(
                1, clientConfig.getCacheRefreshExecutorThreadPoolSize(), 0, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadFactoryBuilder()
                        .setNameFormat("DiscoveryClient-CacheRefreshExecutor-%d")
                        .setDaemon(true)
                        .build()
        );  // use direct handoff
        
        eurekaTransport = new EurekaTransport();
        // 调度服务端端点
        scheduleServerEndpointTask(eurekaTransport, args);
        // 支持dns和配置 做region映射
        AzToRegionMapper azToRegionMapper;
        if (clientConfig.shouldUseDnsForFetchingServiceUrls()) {
            azToRegionMapper = new DNSBasedAzToRegionMapper(clientConfig);
        } else {
            azToRegionMapper = new PropertyBasedAzToRegionMapper(clientConfig);
        }
        if (null != remoteRegionsToFetch.get()) {
            azToRegionMapper.setRegionsToFetch(remoteRegionsToFetch.get().split(","));
        }
        instanceRegionChecker = new InstanceRegionChecker(azToRegionMapper, clientConfig.getRegion());
    } catch (Throwable e) {
        throw new RuntimeException("Failed to initialize DiscoveryClient!", e);
    }
    // fetchRegistry方法，第一次获取全部，之后是增量获取，也可以通过true参数强制全量获取，如果获取成功则返回true，如果客户端和服务端存在问题，则会返回false
    // 如果需要拉取注册表，且获取注册表失败，则从本地备份中获取注册信息，也就是ca的高可用的实现。
    if (clientConfig.shouldFetchRegistry() && !fetchRegistry(false)) {
        fetchRegistryFromBackup();
    }
    // 初始化所有的调度任务 代码见下面
    initScheduledTasks();
    try {
        Monitors.registerObject(this);
    } catch (Throwable e) {
        logger.warn("Cannot register timers", e);
    }

    // This is a bit of hack to allow for existing code using DiscoveryManager.getInstance()
    // to work with DI'd DiscoveryClient
    DiscoveryManager.getInstance().setDiscoveryClient(this);
    DiscoveryManager.getInstance().setEurekaClientConfig(config);

    initTimestampMs = System.currentTimeMillis();
    logger.info("Discovery Client initialized at timestamp {} with initial instances count: {}",
            initTimestampMs, this.getApplications().size());
}
```
  -  **initScheduledTasks 初始化调度任务源码**
```java
/**
 * Initializes all scheduled tasks.
 */
private void initScheduledTasks() {
    // 是否需要拉取注册表，刷新本地缓存注册表
    if (clientConfig.shouldFetchRegistry()) {
        // registry cache refresh timer
        // 默认30秒
        int registryFetchIntervalSeconds = clientConfig.getRegistryFetchIntervalSeconds();
        // 超时后，再次调用的时间间隔基数，默认为10，具体算法可以参考 TimedSupervisorTask的run方法。
        int expBackOffBound = clientConfig.getCacheRefreshExecutorExponentialBackOffBound();
        /**
        public TimedSupervisorTask(String name, ScheduledExecutorService scheduler, ThreadPoolExecutor executor,
                                   int timeout, TimeUnit timeUnit, int expBackOffBound, Runnable task) {
            // 代码省略...
            this.timeoutMillis = timeUnit.toMillis(timeout);
            this.delay = new AtomicLong(timeoutMillis);
            this.maxDelay = timeoutMillis * expBackOffBound;
            // 代码省略...
        }
        
        public void run() {
            Future future = null;
            try {
                // ...
                delay.set(timeoutMillis);
                // ...
            } catch (TimeoutException e) {
                logger.error("task supervisor timed out", e);
                timeoutCounter.increment();
                long currentDelay = delay.get();
                long newDelay = Math.min(maxDelay, currentDelay * 2);
                delay.compareAndSet(currentDelay, newDelay);
            }
            // ...
        }
        **/
        scheduler.schedule(
                new TimedSupervisorTask(
                        "cacheRefresh",
                        scheduler,
                        cacheRefreshExecutor,
                        registryFetchIntervalSeconds,
                        TimeUnit.SECONDS,
                        expBackOffBound,
                        new CacheRefreshThread()
                ),
                registryFetchIntervalSeconds, TimeUnit.SECONDS);
    }
    // 是否发送心跳信息到server，即续约
    if (clientConfig.shouldRegisterWithEureka()) {
        // 每次发起续约时间 默认30秒
        int renewalIntervalInSecs = instanceInfo.getLeaseInfo().getRenewalIntervalInSecs();
        // 默认为10 同理
        int expBackOffBound = clientConfig.getHeartbeatExecutorExponentialBackOffBound();
        logger.info("Starting heartbeat executor: " + "renew interval is: " + renewalIntervalInSecs);

        // Heartbeat timer
        scheduler.schedule(
                new TimedSupervisorTask(
                        "heartbeat",
                        scheduler,
                        heartbeatExecutor,
                        renewalIntervalInSecs,
                        TimeUnit.SECONDS,
                        expBackOffBound,
                        new HeartbeatThread()
                ),
                renewalIntervalInSecs, TimeUnit.SECONDS);
        
        // 更新和同步本地信息到eureka server
        // InstanceInfo replicator
        instanceInfoReplicator = new InstanceInfoReplicator(
                this,
                instanceInfo,
                // 默认30秒
                clientConfig.getInstanceInfoReplicationIntervalSeconds(),
                // 执行速度受限burstSize
                2); // burstSize
        // 状态改变监听器，在下面配置是否需要使用该触发器
        statusChangeListener = new ApplicationInfoManager.StatusChangeListener() {
            @Override
            public String getId() {
                return "statusChangeListener";
            }

            @Override
            public void notify(StatusChangeEvent statusChangeEvent) {
                if (InstanceStatus.DOWN == statusChangeEvent.getStatus() ||
                        InstanceStatus.DOWN == statusChangeEvent.getPreviousStatus()) {
                    // log at warn level if DOWN was involved
                    logger.warn("Saw local status change event {}", statusChangeEvent);
                } else {
                    logger.info("Saw local status change event {}", statusChangeEvent);
                }
                // 触发更新和同步本地消息到erueka server
                instanceInfoReplicator.onDemandUpdate();
            }
        };
        // 是否注册本地状态改变触发器，如果不注册，则不会将本地状态更新，同步到server
        if (clientConfig.shouldOnDemandUpdateStatusChange()) {
            applicationInfoManager.registerStatusChangeListener(statusChangeListener);
        }
        // 设置多少秒后，启动instanceInfoReplicator，默认为40秒
        instanceInfoReplicator.start(clientConfig.getInitialInstanceInfoReplicationIntervalSeconds());
    } else {
        logger.info("Not registering with Eureka server per configuration");
    }
}
```
- **CloudEurekaClient** 主要负责发送心跳信息，方法为onCacheRefreshed

```java
@Override
protected void onCacheRefreshed() {
	if (this.cacheRefreshedCount != null) { //might be called during construction and will be null
		long newCount = this.cacheRefreshedCount.incrementAndGet();
		log.trace("onCacheRefreshed called with count: " + newCount);
		this.publisher.publishEvent(new HeartbeatEvent(this, newCount));
	}
}
```

### EurekaServer初始化 EurekaServerAutoConfiguration
#### EurekaServerConfigBean
    SpringCloud关于EurekaServer的配置项

#### SpringCloud的InstanceRegistry与eureka的InstanceRegistry

- SpringCloud通过**EurekaServerAutoConfiguration**适配，初始化eureka server的信息，即InstanceRegistry

- **InstanceRegistry**类图（此类为SpringCloud定义的InstanceRegistry而非netflix的）
  - LeaseManager 主要负责租约的管理，如创建、更新和删除。
  - LookupService [见LookupService](#LookupService)
  - AbstractInstanceRegistry 处理所有来自eureka client的注册请求
    - 提供注册、续约、注销、过期处理、状态改变处理
    - 增量存储注册表
  - PeerAwareInstanceRegistry 
    - 处理Eureka Server节点间的同步
    - 如果Eureka Server启动的时候会从其他节点获取注册表信息，如果获取失败，Eureka Server会不允许用户在指定的一段时间(EurekaServerConfig.getWaitTimeInMsWhenSyncEmpty)里获取注册表信息。
    - 自我保护实现

![image](http://note.youdao.com/yws/public/resource/474a64dcff40ff49b32f44effeab6b7d/xmlnote/WEBRESOURCE34afd50c521dfff9c945f9161c6f5d37/575) 

- 初始化Eureka Server
**EurekaServerAutoConfiguration 源码**
```java
@Bean
public PeerAwareInstanceRegistry peerAwareInstanceRegistry(
		ServerCodecs serverCodecs) {
	this.eurekaClient.getApplications(); // force initialization
	// 父类AbstractInstanceRegistry初始化一个新的空信息的注册表
	return new InstanceRegistry(this.eurekaServerConfig, this.eurekaClientConfig,
			serverCodecs, this.eurekaClient,
			//每分钟更新次数，默认为1
			this.instanceRegistryProperties.getExpectedNumberOfRenewsPerMin(),
			//确认取消租约时的值，默认为1 
			this.instanceRegistryProperties.getDefaultOpenForTrafficCount());
}

// AbstractInstanceRegistry
/**
 * Create a new, empty instance registry.
 */
protected AbstractInstanceRegistry(EurekaServerConfig serverConfig, EurekaClientConfig clientConfig, ServerCodecs serverCodecs) {
    this.serverConfig = serverConfig;
    this.clientConfig = clientConfig;
    this.serverCodecs = serverCodecs;
    // 用于统计
    this.recentCanceledQueue = new CircularQueue<Pair<Long, String>>(1000);
    this.recentRegisteredQueue = new CircularQueue<Pair<Long, String>>(1000);
    // 记录最近一次更新,每隔1分钟更新一次
    this.renewsLastMin = new MeasuredRate(1000 * 60 * 1);
    // 清理过期增量信息的调度器
    this.deltaRetentionTimer.schedule(getDeltaRetentionTask(),
            // 默认30秒
            serverConfig.getDeltaRetentionTimerIntervalInMs(),
            // 默认30秒
            serverConfig.getDeltaRetentionTimerIntervalInMs());
}

// getDeltaRetentionTask
private TimerTask getDeltaRetentionTask() {
    return new TimerTask() {

        @Override
        public void run() {
            Iterator<RecentlyChangedItem> it = recentlyChangedQueue.iterator();
            while (it.hasNext()) {
                // getRetentionTimeInMSInDeltaQueue 保持增量信息的缓存时间 默认为3分钟
                if (it.next().getLastUpdateTime() <
                        System.currentTimeMillis() - serverConfig.getRetentionTimeInMSInDeltaQueue()) {
                    it.remove();
                } else {
                    break;
                }
            }
        }

    };
}

// PeerAwareInstanceRegistryImpl
@Inject
public PeerAwareInstanceRegistryImpl(
        EurekaServerConfig serverConfig,
        EurekaClientConfig clientConfig,
        ServerCodecs serverCodecs,
        EurekaClient eurekaClient
) {
    super(serverConfig, clientConfig, serverCodecs);
    this.eurekaClient = eurekaClient;
    // 分片数量最近一次更新，每个1分钟更新一次
    this.numberOfReplicationsLastMin = new MeasuredRate(1000 * 60 * 1);
    // We first check if the instance is STARTING or DOWN, then we check explicit overrides,
    // then we check the status of a potentially existing lease.
    // 先检查实例是启动或者关闭(DownOrStartingRule)，然后再检查优先级(InstanceStatus)，再检查可能存在的租约的状态(LeaseExistsRule)UP或者OUT_OF_SERVICE
    // FirstMatchWinsCompositeRule 从状态列表里查找第一个匹配的，如果状态列表都没有匹配的，则使用AlwaysMatchInstanceStatusRule返回默认的状态UP
    this.instanceStatusOverrideRule = new FirstMatchWinsCompositeRule(new DownOrStartingRule(),
            new OverrideExistsRule(overriddenInstanceStatusMap), new LeaseExistsRule());
}
```

#### EurekaServerContext

DefaultEurekaServerContext 继承 EurekaServerContext
- 初始化DefaultEurekaServerContext
  - 本地Eureka Server的上下文，以及暴露给其他组件访问的服务，如注册
  - DefaultEurekaServerContext源码
```java
@PostConstruct
@Override
public void initialize() throws Exception {
    logger.info("Initializing ...");
    // PeerEurekaNode 负责复制所有的节点更新操作。Server端节点更新的主要实现类
    //启动管理同等的eureka节点(PeerEurekaNode)的生命周期 调度器
    peerEurekaNodes.start();
    // 根据节点初始化InstanceRegistry，完成服务端的初始化
    registry.init(peerEurekaNodes);
    logger.info("Initialized");
}

// PeerEurekaNodes
public void start() {
    taskExecutor = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "Eureka-PeerNodesUpdater");
                    thread.setDaemon(true);
                    return thread;
                }
            }
    );
    try {
        // 先解析url，再更新节点信息，先销毁原来的，再更新
        updatePeerEurekaNodes(resolvePeerUrls());
        // 定义更新任务
        Runnable peersUpdateTask = new Runnable() {
            @Override
            public void run() {
                try {
                    updatePeerEurekaNodes(resolvePeerUrls());
                } catch (Throwable e) {
                    logger.error("Cannot update the replica Nodes", e);
                }

            }
        };
        // 调度更新任务
        taskExecutor.scheduleWithFixedDelay(
                peersUpdateTask,
                // 节点跟新间隔 默认 10分钟
                serverConfig.getPeerEurekaNodesUpdateIntervalMs(),
                serverConfig.getPeerEurekaNodesUpdateIntervalMs(),
                TimeUnit.MILLISECONDS
        );
    } catch (Exception e) {
        throw new IllegalStateException(e);
    }
    for (PeerEurekaNode node : peerEurekaNodes) {
        logger.info("Replica node URL:  " + node.getServiceUrl());
    }
}

//PeerEurekaNodes
protected void updatePeerEurekaNodes(List<String> newPeerUrls) {
    if (newPeerUrls.isEmpty()) {
        logger.warn("The replica size seems to be empty. Check the route 53 DNS Registry");
        return;
    }
    // 获取旧的urls，准备关闭
    Set<String> toShutdown = new HashSet<>(peerEurekaNodeUrls);
    toShutdown.removeAll(newPeerUrls);
    // 添加新的urls
    Set<String> toAdd = new HashSet<>(newPeerUrls);
    toAdd.removeAll(peerEurekaNodeUrls);
    // 如果发现没改变，则直接返回
    if (toShutdown.isEmpty() && toAdd.isEmpty()) { // No change
        return;
    }

    // Remove peers no long available
    List<PeerEurekaNode> newNodeList = new ArrayList<>(peerEurekaNodes);
    // 关闭，移除不再可用的urls
    if (!toShutdown.isEmpty()) {
        logger.info("Removing no longer available peer nodes {}", toShutdown);
        int i = 0;
        while (i < newNodeList.size()) {
            PeerEurekaNode eurekaNode = newNodeList.get(i);
            if (toShutdown.contains(eurekaNode.getServiceUrl())) {
                newNodeList.remove(i);
                eurekaNode.shutDown();
            } else {
                i++;
            }
        }
    }

    // Add new peers
    if (!toAdd.isEmpty()) {
        logger.info("Adding new peer nodes {}", toAdd);
        for (String peerUrl : toAdd) {
            // 创建新的urls
            newNodeList.add(createPeerEurekaNode(peerUrl));
        }
    }

    this.peerEurekaNodes = newNodeList;
    this.peerEurekaNodeUrls = new HashSet<>(newPeerUrls);
}

// createPeerEurekaNode
protected PeerEurekaNode createPeerEurekaNode(String peerEurekaNodeUrl) {
    // 创建Jersey 客户端 发送注册、状态更新、心跳等请求
    HttpReplicationClient replicationClient = JerseyReplicationClient.createReplicationClient(serverConfig, serverCodecs, peerEurekaNodeUrl);
    String targetHost = hostFromUrl(peerEurekaNodeUrl);
    if (targetHost == null) {
        targetHost = "host";
    }
    // 创建PeerEurekaNode
    return new PeerEurekaNode(registry, targetHost, peerEurekaNodeUrl, replicationClient, serverConfig);
}

// PeerEurekaNode
PeerEurekaNode(PeerAwareInstanceRegistry registry, String targetHost, String serviceUrl,
                                     HttpReplicationClient replicationClient, EurekaServerConfig config,
                                     int batchSize, long maxBatchingDelayMs,
                                     long retrySleepTimeMs, long serverUnavailableSleepTimeMs) {
    this.registry = registry;
    this.targetHost = targetHost;
    this.replicationClient = replicationClient;

    this.serviceUrl = serviceUrl;
    this.config = config;
    this.maxProcessingDelayMs = config.getMaxTimeForReplication();
    // 获取批处理名称，根据配置的url获取hostName，如果hostName失败，则直接采用配置的url做为批处理名称
    String batcherName = getBatcherName();
    // 执行任务的客户端需要实现的接口，提供了两个接口，一个处理单个任务，一个处理多个任务，都会在同一个时间执行，多个任务的会聚合多个任务的返回结果，且返回的类型是一样的。处理结果有Success, Congestion, TransientError(任务失败，但过会会重试), PermanentError(任务失败，且不再重试)
    ReplicationTaskProcessor taskProcessor = new ReplicationTaskProcessor(targetHost, replicationClient);
    // 创建批处理任务
    this.batchingDispatcher = TaskDispatchers.createBatchingTaskDispatcher(
            batcherName,
            // 批处理任务最大的缓冲区大小,默认1万个，如果超出，则会判断哪些任务已经过期，过期则移除，添加新的任务
            config.getMaxElementsInPeerReplicationPool(),
            // 最大批处理个数，250
            batchSize,
            // 分配给副本的最大线程数 默认20
            config.getMaxThreadsForPeerReplication(),
            // 最大的批处理间隔 500毫秒
            maxBatchingDelayMs,
            // 服务不可用后，休息1秒
            serverUnavailableSleepTimeMs,
            // 网络异常后，每个100毫秒进行重试
            retrySleepTimeMs,
            // 处理任务
            taskProcessor
    );
    this.nonBatchingDispatcher = TaskDispatchers.createNonBatchingTaskDispatcher(
            targetHost,
            // 缓冲区 1万
            config.getMaxElementsInStatusReplicationPool(),
            // 分配给副本处理状态的最大线程数 默认1个
            config.getMaxThreadsForStatusReplication(),
            // 最大的批处理间隔 500毫秒
            maxBatchingDelayMs,
            serverUnavailableSleepTimeMs,
            retrySleepTimeMs,
            taskProcessor
    );
}

//PeerAwareInstanceRegistry
@Override
public void init(PeerEurekaNodes peerEurekaNodes) throws Exception {
    // 启动更新记录器
    this.numberOfReplicationsLastMin.start();
    this.peerEurekaNodes = peerEurekaNodes;
    // 初始化注册信息缓存，以供客户端查询使用，采用的是Guava的堆缓存，具体实现类 ResponseCacheImpl
    initializedResponseCache();
    // 启动检查是否因为网络分区导致的更新急剧下降，从而防止服务被误删，即自我保护模式
    scheduleRenewalThresholdUpdateTask();
    // 初始化获取其他Region区域的注册表信息的调度器
    initRemoteRegionRegistry();

    try {
        Monitors.registerObject(this);
    } catch (Throwable e) {
        logger.warn("Cannot register the JMX monitor for the InstanceRegistry :", e);
    }
}
```

##### PeerEurekaNodes 与 PeerEurekaNode
PeerEurekaNodes类图
![image](http://note.youdao.com/yws/public/resource/474a64dcff40ff49b32f44effeab6b7d/xmlnote/WEBRESOURCEd744649e6128703e1008f47c4df8d640/854)
PeerEurekaNode类图
![image](http://note.youdao.com/yws/public/resource/474a64dcff40ff49b32f44effeab6b7d/xmlnote/WEBRESOURCE3c3e3c3193d439425bf453bc2f36cf1a/866)
- PeerEurekaNodes 通过start方法开启 管理 PeerEurekaNode的生命周期调度
- PeerEurekaNode 节点间交互的主要实现类，如心跳等
  - 定义了当网络异常后，每隔100毫秒进行重试
  - 如果服务器不可用，进行1秒的休眠
  - 定义最大的批处理调度间隔 500毫秒
  - 定义了最大的批处理请求250

#### EurekaServerBootstrap
初始化完成EurekaServerContext后，接下来创建EurekaServerBootstrap(其同com.netflix.eureka.EurekaBootStrap的代码基本一致，netflix是通过监听ServletContextListener事件来启动eureka server),
再通过EurekaServerInitializerConfiguration启动线程调用EurekaServerBootstrap的contextInitialized方法
```java
@Override
public void start() {
    new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                //TODO: is this class even needed now?
                // 调用contextInitialized Eureka Server进行初始化
                eurekaServerBootstrap.contextInitialized(EurekaServerInitializerConfiguration.this.servletContext);
                log.info("Started Eureka Server");
                // 发布相关事件
                publish(new EurekaRegistryAvailableEvent(getEurekaServerConfig()));
                EurekaServerInitializerConfiguration.this.running = true;
                publish(new EurekaServerStartedEvent(getEurekaServerConfig()));
            }
            catch (Exception ex) {
                // Help!
                log.error("Could not initialize Eureka servlet context", ex);
            }
        }
    }).start();
}

// EurekaServerBootstrap
public void contextInitialized(ServletContext context) {
    try {
        // 初始化环境配置信息，eureka.datacenter和eureka.environment，如果没配置第一个为default，第二个为test
        initEurekaEnvironment();
        // 初始化Eureka Server Context，调用PeerAwareInstanceRegistryImpl同步注册信息
        initEurekaServerContext();
        // 将EurekaServerContext设置到ServletContext中
        context.setAttribute(EurekaServerContext.class.getName(), this.serverContext);
    }
    catch (Throwable e) {
        log.error("Cannot bootstrap eureka server :", e);
        throw new RuntimeException("Cannot bootstrap eureka server :", e);
    }
}
```
至此，eureka server也就启动完成了，接下来都是通过调度来实现交互。


## 总结
- EurekaClient的责任主要提现在DiscoveryClient类的实现；
- EurekaServer的责任主要体现在PeerEurekaNodes与PeerEurekaNode类的实现；

- 其他相关类：
    - 客户端配置：EurekaClientConfigBean
    - 服务端配置：EurekaServerConfigBean
    - 服务注册操作类：SpringCloud与netflix的InstanceRegistry
    - EurekaServer启动类：SpringCloud的EurekaServerBootstrap
