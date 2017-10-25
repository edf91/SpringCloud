package org.wxd.springcloud.u.consumer.web;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.wxd.springcloud.u.consumer.domain.User;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by wangxd on 2017/9/16.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@RestController
public class UserCtl {

    @Resource
    private RestTemplate restTemplate;
    @Resource
    private DiscoveryClient discoveryClient;
    @Resource
    private LoadBalancerClient loadBalancerClient;

    @HystrixCommand(fallbackMethod = "ofIdFallback")
    @GetMapping(value = "/c/user/{id}")
    public User ofId(@PathVariable("id") String id){
        return restTemplate.getForObject("http://service/user/" + id,User.class);
    }

    /**
     * 调用dubbo服务
     * @param id
     * @return
     */
    @HystrixCommand(fallbackMethod = "ofIdFallback")
    @GetMapping(value = "/c/dubbo/{id}")
    public User ofIdDubbo(@PathVariable("id") String id){
        return restTemplate.getForObject("http://sidecar/dubbo/" + id,User.class);
    }

    /**
     * 熔断返回的数据
     * @param id
     * @return
     */
    public User ofIdFallback(String id){
        User user = new User();
        user.setId("fallback");
        user.setName("fallbackName");
        return user;
    }

    /**
     * 查询服务信息
     * @return
     */
    @GetMapping("/instance/info")
    public List<ServiceInstance> showInfo(){
        return this.discoveryClient.getInstances("service");
    }

    /**
     * 显示当前选择的是哪个节点
     * @return
     */
    @GetMapping("/choose")
    public ServiceInstance choose(){
        return this.loadBalancerClient.choose("service");
    }
}
