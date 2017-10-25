package org.wxd.springcloud.dubbo.api;

/**
 * Created by wangxd on 2017/10/25.
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
