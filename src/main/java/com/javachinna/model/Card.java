package com.javachinna.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "card")

public class Card implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Long number;
    private Double balance ;
    private String holderName;
    private String exp_month;
    private String exp_year;
    private Integer cvc;
    private String StripeApiId;
    private Boolean isDefault;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Client owner;
}
