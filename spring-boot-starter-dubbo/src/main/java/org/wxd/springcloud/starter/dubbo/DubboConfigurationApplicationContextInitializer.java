package org.wxd.springcloud.starter.dubbo;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.core.env.Environment;

import com.alibaba.dubbo.config.spring.AnnotationBean;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class DubboConfigurationApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        String scan = applicationContext.getEnvironment().getProperty("spring.dubbo.scan");
        if (Objects.isNull(scan)) return;

        AnnotationBean scanner = BeanUtils.instantiate(AnnotationBean.class);
        scanner.setPackage(scan);
        scanner.setApplicationContext(applicationContext);
        applicationContext.addBeanFactoryPostProcessor(scanner);
        applicationContext.getBeanFactory().addBeanPostProcessor(scanner);
        applicationContext.getBeanFactory().registerSingleton("annotationBean", scanner);

    }


}
