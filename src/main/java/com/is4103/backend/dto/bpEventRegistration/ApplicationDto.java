package com.is4103.backend.dto.bpEventRegistration;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.is4103.backend.model.Booth;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ApplicationDto {

    private Long eventId;

    private Integer boothQty;

    private UUID id;

    private String description;

    private String comments;

    private String paymentMethodId;

    private List<Booth> booths;
}
