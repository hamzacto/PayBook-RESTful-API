package com.myProject.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDate;

@Data

@Entity

@Table(name="operation")
public class operation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sending_client_id")
    @Autowired
    private Client sendingClient;

    @ManyToOne
    @JoinColumn(name = "reciving_client_id")
    @Autowired
    private Client recivingClient;

    @Column(nullable = false)
    private Double montant;

    private LocalDate date ;

    public operation(Client sendingClient, Client recivingClient, Double montant, LocalDate date) {
        this.sendingClient = sendingClient;
        this.recivingClient = recivingClient;
        this.montant = montant;
        this.date = date;
    }

    public operation() {
    }
}


