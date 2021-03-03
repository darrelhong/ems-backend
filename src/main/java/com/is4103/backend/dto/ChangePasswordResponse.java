package com.is4103.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordResponse {
    private String message;

    public ChangePasswordResponse(String message) {
        this.message = message;
    }
}