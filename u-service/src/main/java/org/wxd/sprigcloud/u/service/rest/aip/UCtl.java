package org.wxd.sprigcloud.u.service.rest.aip;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.wxd.sprigcloud.u.service.domain.User;
import org.wxd.sprigcloud.u.service.jpa.UService;

/**
 * Created by wangxd on 2017/9/16.
 */
@RestController
public class UCtl {
    @Autowired
    UService service;


    @HystrixCommand(fallbackMethod = "ofIdFallBack")
    @GetMapping(value = "/user/{id}")
    public User ofId(@PathVariable("id") String id) {
        Integer v = Integer.parseInt(id);
        return service.ofId(id);
    }

    public User ofIdFallBack(String id){
        return service.ofId("1");
    }


}
