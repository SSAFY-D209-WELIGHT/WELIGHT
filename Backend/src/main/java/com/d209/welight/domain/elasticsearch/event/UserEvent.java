package com.d209.welight.domain.elasticsearch.event;
import com.d209.welight.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserEvent {
    private final String eventType; // CREATE, UPDATE, DELETE
    private final User user;
}