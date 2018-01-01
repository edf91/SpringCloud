package org.wxd.junit.demo.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "t_person")
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "p_name")
    private String name;
}
