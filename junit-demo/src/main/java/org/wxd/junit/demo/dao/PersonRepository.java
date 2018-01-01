package org.wxd.junit.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.wxd.junit.demo.domain.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person,Integer>{
}