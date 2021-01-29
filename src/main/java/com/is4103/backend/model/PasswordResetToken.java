package com.is4103.backend.model;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PasswordResetToken extends Token {

    public PasswordResetToken() {
        super();
    }

    public PasswordResetToken(final String token) {
        super(token);
    }

    public PasswordResetToken(final String token, final User user) {
        super(token, user);
    }
}
