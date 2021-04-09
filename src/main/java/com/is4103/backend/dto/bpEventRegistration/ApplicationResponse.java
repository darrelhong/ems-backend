package com.is4103.backend.dto.bpEventRegistration;

import com.is4103.backend.model.SellerApplication;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApplicationResponse {

    private Double paymentAmount;

    private String clientSecret;

    private SellerApplication sellerApplication;

}
