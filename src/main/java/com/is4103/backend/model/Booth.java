package com.is4103.backend.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

import java.util.List;

import javax.persistence.Column;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@JsonView(EventViews.Private.class)
public class Booth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    @ManyToMany
    @JsonIgnoreProperties("booths")
    private List<Product> products;

    // @ManyToOne
    // private Event event;

    @ManyToOne
    @JsonIgnoreProperties("booths")
    private SellerProfile sellerProfile;

    private int boothNumber;

    private String description;
}
