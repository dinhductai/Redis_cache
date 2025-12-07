package com.redis.redis_cache.service;

import com.redis.redis_cache.dto.request.UserRequest;
import com.redis.redis_cache.dto.response.UserResponse;
import com.redis.redis_cache.entity.User;

public interface UserService {
    UserResponse getUserById(Long id);
    UserResponse saveOrUpdateUser(UserRequest user);

}
