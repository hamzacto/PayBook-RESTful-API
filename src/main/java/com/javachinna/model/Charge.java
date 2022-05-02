package com.javachinna.model;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class Charge implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private Double amout;
    private String currency;
    private String StripeApiId;
    @ManyToOne
    @JoinColumn(name = "charged_card_id")
    private Card chargedCard;
}
