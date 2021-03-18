package com.is4103.backend.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.is4103.backend.util.validation.ValidEmail;

import org.springframework.lang.Nullable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendEnquiryRequest {
    @NotNull
    @NotEmpty
    @ValidEmail
    private String senderEmail;

    @NotNull
    @NotEmpty
    @ValidEmail
    private String receiverEmail;

    @NotNull
    @NotEmpty
    private String subject;
    @NotNull
    @NotEmpty
    private String content;
    @Nullable
    private Long eventId;
    
}
