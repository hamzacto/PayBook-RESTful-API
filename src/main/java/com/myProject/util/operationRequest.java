package com.myProject.util;


import lombok.Data;

@Data
public class operationRequest {
    private Integer sendingid ;
    private String recievingEmail;
    private Double montant;
}
