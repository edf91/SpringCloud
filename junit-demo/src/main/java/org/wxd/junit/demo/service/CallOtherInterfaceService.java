package org.wxd.junit.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wxd.junit.demo.api.OtherInterface;

@Service
public class CallOtherInterfaceService {

    @Autowired
    private OtherInterface otherInterface;


    public String of(String id){
        return otherInterface.getOf(id);
    }
}
