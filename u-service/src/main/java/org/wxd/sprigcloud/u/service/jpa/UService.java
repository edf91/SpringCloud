package org.wxd.sprigcloud.u.service.jpa;


import org.springframework.stereotype.Service;
import org.wxd.sprigcloud.u.service.domain.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangxd on 2017/9/16.
 */
@Service
public class UService {
    private static Map<String,User> repository = new HashMap<>();


    static {
        repository.put("1",new User().id("1").name("1name"));
        repository.put("2",new User().id("2").name("2name"));
        repository.put("3",new User().id("3").name("3name"));
    }

    public User ofId(String id){
        return repository.get(id);
    }
}
