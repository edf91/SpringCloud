package org.wxd.junit.demo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.wxd.junit.demo.service.SimpleService;

@RestController
public class SimpleController {
    @Autowired
    private SimpleService service;

    @RequestMapping(value = "/simple/{id}",method = RequestMethod.GET)
    public String of(@PathVariable String id){
        return service.of(id);
    }

    @RequestMapping(value = "/simple/{id}/remove",method = RequestMethod.DELETE)
    public void delOf(@PathVariable String id){
        service.delOf(id);
    }

    @RequestMapping(value = "/simple/add",method = RequestMethod.POST)
    public void add(String id){
        service.save(id);
    }
}
