package com.is4103.backend.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCardRequest {
    
    private String cardholdername;
    private String cardnumber;
    private String expMth;
    private String expYear;
    private String cvc;
}
