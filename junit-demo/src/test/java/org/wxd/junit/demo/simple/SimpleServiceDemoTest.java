package org.wxd.junit.demo.simple;


import org.hamcrest.core.Is;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wxd.junit.demo.service.SimpleService;

import java.util.Objects;

/**
 * 简单方法 单元测试
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(value = MethodSorters.JVM)
public class SimpleServiceDemoTest {
    @Autowired
    private SimpleService service;

    private static String id = "id";

    @Before
    public void setUp(){
        service.save(id);
    }


    @Test
    public void assertOfId(){
        String qId = service.of(id);
        Assert.assertEquals(id,qId);
        Is.is(Objects.equals(id,qId));
    }

    @Test
    public void assertDelOfId(){
        service.delOf(id);
        String qId = service.of(id);

        Assert.assertNull(qId);

    }

    @After
    public void clearUp(){
        service.delOf(id);
    }

}
