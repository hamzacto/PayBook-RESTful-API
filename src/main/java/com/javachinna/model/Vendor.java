package com.javachinna.model;


import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Vendor{
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double Balance;
    private String email;
    private String password;
}
