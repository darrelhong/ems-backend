package com.is4103.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AddCardResponse {
   private  String message;
    public AddCardResponse(String message){
        this.message = message;
    }
}
