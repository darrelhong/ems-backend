package com.is4103.backend.dto;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UpdateProductRequest {
    private MultipartFile file;

    private Long pid;

    private String name;

    private String description;
}
