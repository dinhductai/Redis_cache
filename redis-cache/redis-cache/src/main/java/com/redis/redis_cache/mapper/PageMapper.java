package com.redis.redis_cache.mapper;

import com.redis.redis_cache.dto.response.PageResponse;
import com.redis.redis_cache.dto.response.UserResponse;
import com.redis.redis_cache.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PageMapper {
    private final UserMapper userMapper;

    public PageResponse<UserResponse> pageToResponse(final Page<User> pageData, int page, int size) {
        return PageResponse.<UserResponse>builder()
                .pageNo(page)
                .pageSize(size)
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .content(pageData.getContent().stream()
                        .map(userMapper::entityToResponse)
                        .toList())
                .build();
    }
}
