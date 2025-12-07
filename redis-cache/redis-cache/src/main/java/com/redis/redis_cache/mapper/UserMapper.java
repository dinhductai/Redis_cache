package com.redis.redis_cache.mapper;

import com.redis.redis_cache.dto.request.UserRequest;
import com.redis.redis_cache.dto.response.UserResponse;
import com.redis.redis_cache.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse entityToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public User requestToEntity(UserRequest user) {
        return User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
