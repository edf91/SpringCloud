package org.wxd.sprigcloud.u.service.rest.aip;

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

    @GetMapping(value = "/user/{id}")
    public User ofId(@PathVariable("id") String id) {
        return service.ofId(id);
    }

}
