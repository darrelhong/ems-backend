package com.is4103.backend.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganiserSearchCriteria {
    Integer size = 10;
    Integer page;
    String sort;
    String sortDir;

    String keyword;

    public PageRequest toPageRequest() {
        if (sort != null && sortDir != null) {
            if (sortDir.equals("desc")) {
                return PageRequest.of(page, size, Sort.by(sort).descending());
            }
            return PageRequest.of(page, size, Sort.by(sort).ascending());
        }
        return PageRequest.of(page, size);
    }
}