package com.is4103.backend.dto;

import java.util.Set;

import com.is4103.backend.model.Role;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginUserResponse {
    private Long id;
    private String name;
    private String description;
    private String profilePic;
    private String email;
    private Boolean enabled;
    private String phonenumber;
    private String address;
    private Set<Role> roles;
}