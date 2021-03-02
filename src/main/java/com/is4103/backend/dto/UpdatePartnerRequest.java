package com.is4103.backend.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePartnerRequest {

    @NotNull
    private Long id;

    @NotNull
    @NotEmpty
    private String name;

    private String address;
    @NotNull
    @NotEmpty
    private String phonenumber;

    private String profilepic;

    private String description;

    private boolean enabled;

    private String businessCategory;
    



}
