package com.hive.identity.event;

import com.hive.common.event.DomainEvent;
import lombok.Getter;

@Getter
public class UserCreatedEvent extends DomainEvent {
    private final String userId;
    private final String username;
    private final String email;

    public UserCreatedEvent(String userId, String username, String email) {
        super();
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
}
