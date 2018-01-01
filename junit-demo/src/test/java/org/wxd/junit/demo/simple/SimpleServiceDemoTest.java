package org.wxd.junit.demo.simple;


import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wxd.junit.demo.service.SimpleService;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

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
    public void setUp() {
        service.save(id);
    }


    @Test
    public void assertOfId() {
        String qId = service.of(id);
        assertThat(qId, is(id));
    }

    @Test
    public void assertDelOfId() {
        service.delOf(id);
        String qId = service.of(id);

        assertNull(qId);

    }

    // 异常测试
    @Test(expected = IndexOutOfBoundsException.class)
    public void assertException() {
        service.error();
    }

    @After
    public void clearUp() {
        service.delOf(id);
    }

}
