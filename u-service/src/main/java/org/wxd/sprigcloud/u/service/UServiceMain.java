package org.wxd.sprigcloud.u.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Created by wangxd on 2017/9/16.
 * 服务端
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UServiceMain {

    public static void main(String[] args) {
        SpringApplication.run(UServiceMain.class, args);
    }
}
