package com.is4103.backend.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pid;

    @Column(nullable = false)
    @JsonIgnoreProperties("products")
    @ManyToMany(mappedBy = "products")
    private List<Booth> booths;

    private String name;

    private String description;

    private String image;

    @ManyToOne
    @ElementCollection(targetClass = BusinessPartner.class)
    // @JsonIgnore
    @JsonIgnoreProperties("products")
    private BusinessPartner businessPartner;

}