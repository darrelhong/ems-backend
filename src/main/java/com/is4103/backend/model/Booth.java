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
    private Long id;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = true)
    private Double length;

    @Column(nullable = true)
    private Double width;

    @Column(nullable = true)
    @ManyToMany
    private List<Product> products;

    @ManyToOne
    private Event event;

    public Booth() {
    };

    public Booth(Double price, Double length, Double width, Event event) {
        super();
        this.price = price;
        this.length = length;
        this.width = width;
        this.event = event;
    }

    public Booth(Double price, Double length, Double width) {
        super();
        this.price = price;
        this.length = length;
        this.width = width;
    }
}
