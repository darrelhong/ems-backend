package com.is4103.backend.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

import lombok.Data;

@Entity
@Data// This plugin automatically generates constructors, getters/setters
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection
    @JoinTable(name = "authorities", joinColumns = { @JoinColumn(name = "email") })
    @Column(name = "authority")
    private Set<String> roles;
}
