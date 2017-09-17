package org.wxd.springcloud.turbine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.turbine.EnableTurbine;

/**
 * Created by wangxd on 2017/9/17.
 * turbine
 */
@SpringBootApplication
@EnableTurbine
public class TurbineMain {

    public static void main(String[] args) {
        SpringApplication.run(TurbineMain.class,args);
    }
}
