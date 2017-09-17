package org.wxd.springcloud.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * Created by wangxd on 2017/9/18.
 * zull
 */
@SpringBootApplication
@EnableZuulProxy
public class ZuulMain {
    public static void main(String[] args) {
        SpringApplication.run(ZuulMain.class,args);
    }
}
