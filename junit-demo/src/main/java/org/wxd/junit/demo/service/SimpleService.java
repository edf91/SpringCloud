package org.wxd.junit.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wxd.junit.demo.facade.SimpleFacade;

@Service
public class SimpleService {

    @Autowired
    private SimpleFacade facade;

    public String of(String id){
        return facade.of(id);
    }

    public void delOf(String id){
        facade.delOf(id);
    }

    public void save(String id){
        facade.persist(id);
    }




}
