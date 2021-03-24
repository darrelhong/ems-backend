package com.is4103.backend.model;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import lombok.Data;

@Entity
@Data // This plugin automatically generates constructors, getters/setters
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = true)
    private String profilePic;
    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    // currently used to check if user email is verified, can be used to
    // lock/disable account in the future
    @Column(nullable = false)
    private boolean enabled = false;

    @Column(nullable = true)
    private String phonenumber;

    @Column(nullable = true)
    private String address;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "USER_ROLES", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = {
            @JoinColumn(name = "ROLE_ID") })
    private Set<Role> roles;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @Fetch(value = FetchMode.SUBSELECT)
    @ElementCollection(targetClass = Notification.class)
    private List<Notification> notifications;

    @Column(nullable = true)
    private String paymentMethodId;
    
    @Column(nullable = true)
    private boolean systemEmailNoti;
   
    @Column(nullable = true)
    private boolean eoEmailNoti;
}
