package org.wxd.sprigcloud.u.service.domain;

/**
 * Created by wangxd on 2017/9/16.
 */
public class User {
    String id;
    String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    

    public User id(String id) {
        this.id = id;
        return this;
    }

    public User name(String name) {
        this.name = name;
        return this;
    }
}
