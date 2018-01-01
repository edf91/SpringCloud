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
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@FixMethodOrder(value = MethodSorters.JVM)
public class SimpleControllerDemoTest {

    MockMvc mvc;
    @Autowired
    WebApplicationContext context;
    static String id = "id";

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();

        mvc.perform(MockMvcRequestBuilders.post("/simple/add").param("id", id))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void assertOfId() throws Exception {
        String qId = mvc.perform(MockMvcRequestBuilders.get(String.format("/simple/%s", id)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(qId, is(id));
    }

    @Test
    public void assertDel() throws Exception {
        remove();
        String qId = mvc.perform(MockMvcRequestBuilders.get(String.format("/simple/%s", id)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(qId, is(""));
    }


    private void remove() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders.delete(String.format("/simple/%s/remove", id))
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }


    @After
    public void clearUp() throws Exception {
        remove();
    }
}
