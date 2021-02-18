package com.is4103.backend.dto;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DisabledAccountRequest {

    @NotNull
    private Long id;
<<<<<<< HEAD
    
    private boolean enabled;

=======

    private boolean enabled;
>>>>>>> 1f0cc9a148eb094aba18f609a470931ee448c5bb

}
