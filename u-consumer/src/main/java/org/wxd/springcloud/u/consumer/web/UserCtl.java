package org.wxd.springcloud.u.consumer.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
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

    @Autowired
    private RestTemplate restTemplate;
    @Resource
    private DiscoveryClient discoveryClient;

    @GetMapping(value = "/c/user/{id}")
    public User ofId(@PathVariable("id") String id){

        return restTemplate.getForObject("http://service/user/" + id,User.class);
    }

    /**
     * 查询服务信息
     * @return
     */
    @GetMapping("/instance")
    public List<ServiceInstance> showInfo(){
        return this.discoveryClient.getInstances("service");
    }

}
