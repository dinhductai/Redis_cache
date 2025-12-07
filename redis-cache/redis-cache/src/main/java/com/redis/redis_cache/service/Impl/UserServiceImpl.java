package com.redis.redis_cache.service.Impl;

import com.redis.redis_cache.dto.request.UserRequest;
import com.redis.redis_cache.dto.response.PageResponse;
import com.redis.redis_cache.dto.response.UserResponse;
import com.redis.redis_cache.entity.User;
import com.redis.redis_cache.repository.UserRepository;
import com.redis.redis_cache.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor 
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@Transactional
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Override
    @Cacheable(value = "users",key = "#id")
    public UserResponse getUserById(Long id) {
        // Giả lập độ trễ DB (để bạn thấy rõ sự khác biệt khi có cache)
        try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return entityToResponse(user);
    }

    @Override
    @CacheEvict(value = "users", key = "#user.id", condition = "#user.id != null")
    public UserResponse saveOrUpdateUser(UserRequest user) {
        log.info("Saving User ID: {}. Evicting cache...", user.getId());

        User savedUser = userRepository.save(requestToEntity(user));
        return entityToResponse(savedUser);
    }

    @Override
    @Cacheable(value = "user_list", key = "'search:' + #keyword + '-page:' + #page + '-size:' + #size")
    public PageResponse<UserResponse> searchUsers(String keyword, int page, int size) { // Đổi return type
        try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> pageData = userRepository.findByUsernameContainingIgnoreCase(keyword, pageable);

        return PageResponse.<UserResponse>builder()
                .pageNo(page)
                .pageSize(size)
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .content(pageData.getContent().stream()
                        .map(this::entityToResponse)
                        .toList())
                .build();
    }

    private UserResponse entityToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    private User requestToEntity(UserRequest user) {
        return User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
