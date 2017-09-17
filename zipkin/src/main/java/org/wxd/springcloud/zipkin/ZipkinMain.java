package org.wxd.springcloud.zipkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import zipkin.server.EnableZipkinServer;

/**
 * Created by wangxd on 2017/9/17.
 * zipkin
 */
@SpringBootApplication
@EnableZipkinServer
public class ZipkinMain {
    public static void main(String[] args) {
        SpringApplication.run(ZipkinMain.class,args);
    }
}
