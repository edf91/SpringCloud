package org.wxd.springcloud.dubbo.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.wxd.springcloud.dubbo.api.UService;
import org.wxd.springcloud.dubbo.api.User;

/**
 * Created by wangxd on 2017/10/25.
 */
@RestController
public class URestService {

    @Autowired
    private UService uService;

    @GetMapping("/dubbo/{id}")
    public User ofId(@PathVariable("id")String id) {
        return uService.ofId(id);
    }
}
