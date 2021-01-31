package com.is4103.backend.dto;

import com.is4103.backend.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    private AuthToken authToken;
    private User user;
}
