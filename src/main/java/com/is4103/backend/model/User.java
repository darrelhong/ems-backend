package com.is4103.backend.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import lombok.Data;

@Entity
@Data // This plugin automatically generates constructors, getters/setters
public class User {

        @Id
        @GeneratedValue
        private Long id;

        private String name;

        @Column(nullable = false, unique = true)
        private String email;

        @Column(nullable = false)
        private String password;

        @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
        @JoinTable(name = "USER_ROLES", joinColumns = {
                        @JoinColumn(name = "USER_ID")
        }, inverseJoinColumns = {
                        @JoinColumn(name = "ROLE_ID") })
        private Set<Role> roles;
}
