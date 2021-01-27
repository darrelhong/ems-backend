package com.is4103.backend.util.validation.registration;

import com.is4103.backend.model.User;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    static final long serialVersionUID = 1L;

    private User user;

    public OnRegistrationCompleteEvent(final User user) {
        super(user);

        this.user = user;
    }
}
