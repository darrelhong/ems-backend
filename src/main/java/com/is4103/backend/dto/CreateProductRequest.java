package com.is4103.backend.dto;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreateProductRequest {
    private MultipartFile file;

    private String name;

    private String description;
}
