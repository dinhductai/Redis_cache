package com.redis.redis_cache.service;

import com.redis.redis_cache.dto.request.UserRequest;
import com.redis.redis_cache.dto.response.PageResponse;
import com.redis.redis_cache.dto.response.UserResponse;
import com.redis.redis_cache.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    UserResponse getUserById(Long id);
    UserResponse saveOrUpdateUser(UserRequest user);
    PageResponse<UserResponse> searchUsers(String keyword, int page, int size);
    void deleteUser(Long id);
}
