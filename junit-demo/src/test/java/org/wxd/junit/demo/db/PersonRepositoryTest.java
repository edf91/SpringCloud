package org.wxd.junit.demo.db;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.wxd.junit.demo.dao.PersonRepository;
import org.wxd.junit.demo.domain.Person;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@FixMethodOrder(value = MethodSorters.JVM)
public class PersonRepositoryTest {

    @Autowired
    private PersonRepository repository;


    @Test
    public void assertFindById() {
        Integer id = 1;
        Person person = repository.findOne(id);
        assertNotNull(person);
        assertThat(person.getName(), is("test1"));
    }

    @Test
    public void assertDelById() {
        Integer id = 1;
        repository.delete(id);
        Person person = repository.findOne(id);
        assertNull(person);
    }
}
