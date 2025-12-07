package com.redis.redis_cache.event;
import com.redis.redis_cache.dto.response.UserResponse;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserSavedEvent extends ApplicationEvent {
    private final UserResponse user;

    public UserSavedEvent(Object source, UserResponse user) {
        super(source);
        this.user = user;
    }
}