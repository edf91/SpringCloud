package org.wxd.springcloud.sidecar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.sidecar.EnableSidecar;

/**
 * Created by wangxd on 2017/10/24.
 */
@SpringBootApplication
@EnableSidecar
public class SidecarMain {

    public static void main(String[] args) {
        SpringApplication.run(SidecarMain.class,args);
    }
}
