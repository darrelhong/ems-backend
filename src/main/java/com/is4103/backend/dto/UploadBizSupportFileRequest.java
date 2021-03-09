package com.is4103.backend.dto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UploadBizSupportFileRequest {
   
    @NotNull
    private Long id;

}
