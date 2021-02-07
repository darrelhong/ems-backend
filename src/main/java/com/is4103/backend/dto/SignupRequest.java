package com.is4103.backend.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.is4103.backend.util.validation.ValidEmail;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class SignupRequest {
    @NotNull
    @NotEmpty
    @ValidEmail
    private String email;

    @NotNull
    @NotEmpty
    private String password;

    @NotNull
    @NotEmpty
    private String name;

}
