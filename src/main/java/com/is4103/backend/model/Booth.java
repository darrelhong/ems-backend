package com.is4103.backend.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import lombok.Data;

import java.util.List;

import javax.persistence.Column;

@Entity
@Data

public class Booth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bid;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = true)
    private double length;

    @Column(nullable = true)
    private double width;

    @Column(nullable = true)
    @ManyToMany
    private List<Product> products;

    @ManyToOne
    private Event event;
}
