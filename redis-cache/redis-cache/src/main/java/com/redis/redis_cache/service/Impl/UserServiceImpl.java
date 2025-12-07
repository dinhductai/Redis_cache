package com.redis.redis_cache.service.Impl;

import com.redis.redis_cache.dto.request.UserRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j // 1. Logging chuẩn chuyên nghiệp
@RequiredArgsConstructor // 2. Constructor Injection (Best practice)
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@Transactional
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Override
    @Cacheable(value = "users",key = "#id")
    public UserResponse getUserById(Long id) {
        log.info("Fetching User ID: {} from Database...", id); // Chỉ hiện dòng này khi Cache Miss (chưa có cache)

        // Giả lập độ trễ DB (để bạn thấy rõ sự khác biệt khi có cache)
        try { Thread.sleep(3000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // 3. Mapping Entity -> DTO (Redis sẽ cache cái DTO này)
        return entityToResponse(user);
    }

    @Override
    @CacheEvict(value = "users", key = "#user.id", condition = "#user.id != null")
    public UserResponse saveOrUpdateUser(UserRequest user) {
        log.info("Saving User ID: {}. Evicting cache...", user.getId());

        User savedUser = userRepository.save(requestToEntity(user));
        return entityToResponse(savedUser);
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
