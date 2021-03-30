package com.is4103.backend.dto;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BroadcastMessageToFollowersRequest {
    
    @NotEmpty
    @NotNull
    @NotEmpty
    private String content;
    @NotNull
    @NotEmpty
    private String broadcastOption;
}
