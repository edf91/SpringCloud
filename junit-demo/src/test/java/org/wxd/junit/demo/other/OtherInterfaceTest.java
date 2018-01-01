package org.wxd.junit.demo.other;


import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wxd.junit.demo.api.OtherInterface;
import org.wxd.junit.demo.service.CallOtherInterfaceService;

import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@FixMethodOrder(value = MethodSorters.JVM)
public class OtherInterfaceTest {

    @MockBean
    private OtherInterface otherInterface;
    @Autowired
    private CallOtherInterfaceService service;

    private String id = "id";

    @Before
    public void setUp(){
        /**
         * 如果otherInterface 本地开发未完成，或者网络不通等，则可以通过mock打桩
         */
        when(otherInterface.getOf(id)).thenReturn(id);
    }

    // 测试otherInterface
    @Test
    public void assertOfId(){
        String qId = otherInterface.getOf(id);
        Assert.assertEquals(qId,id);
        // 校验 getOf(id) 是否被调用1次
        verify(otherInterface,times(1)).getOf(id);
    }

    // 测试通过service 调用 otherService
    @Test
    public void assertCallOfId(){
        String qId = service.of(id);
        Assert.assertEquals(qId,id);
        verify(otherInterface,times(1)).getOf(id);
    }

}
