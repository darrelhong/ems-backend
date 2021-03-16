package com.is4103.backend.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.is4103.backend.util.validation.ValidEmail;

import org.springframework.lang.Nullable;

public class SendEnquiryRequest {
    @NotNull
    @NotEmpty
    @ValidEmail
    private String senderEmail;

    @NotNull
    @NotEmpty
    @ValidEmail
    private String receiverEmail;

    @Nullable
    private String subject;
    @Nullable
    private String content;
    
}
