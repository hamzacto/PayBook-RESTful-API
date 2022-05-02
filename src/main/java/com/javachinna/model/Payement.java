package com.javachinna.model;

import lombok.Data;

import java.time.LocalDate;
@Data
public class Payement {
    protected Double amount;
    protected LocalDate payementDate;
    protected Product product;
    protected Client client;
}
