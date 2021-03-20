package com.is4103.backend.dto;

import java.util.Set;

import com.is4103.backend.model.Role;

public interface LoginUserResponse {
    Long getId();

    String getName();

    String getDescription();

    String getProfilePic();

    String getEmail();

    Boolean getEnabled();

    String getPhonenumber();

    String getAddress();

    Set<Role> getRoles();
}