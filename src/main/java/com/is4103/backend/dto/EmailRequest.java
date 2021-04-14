package com.is4103.backend.dto;

import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
public class EmailRequest {

    private Long senderId;

    private Long recipientId;

    private String subject;

    private String textBody;

    //BY DEFAULT SHLD JUST BE THE "SENDERs EMAIL ADDRESS"
    // private String ccAddress; 

}