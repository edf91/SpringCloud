package org.wxd.junit.demo.facade.impl;

import org.springframework.stereotype.Repository;
import org.wxd.junit.demo.facade.SimpleFacade;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SimpleFacadeImpl implements SimpleFacade{

    private static Map<String,String> map = new ConcurrentHashMap<>();

    @Override
    public String of(String id) {
        return map.get(id);
    }

    @Override
    public void delOf(String id) {
        map.remove(id);
        return;
    }

    @Override
    public void persist(String id) {
        map.put(id,id);
    }
}
