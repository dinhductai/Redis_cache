package com.redis.redis_cache.service.Impl;

import com.redis.redis_cache.constant.CacheConst;
import com.redis.redis_cache.dto.request.UserRequest;
import com.redis.redis_cache.dto.response.PageResponse;
import com.redis.redis_cache.dto.response.UserResponse;
import com.redis.redis_cache.entity.User;
import com.redis.redis_cache.event.UserSavedEvent;
import com.redis.redis_cache.mapper.PageMapper;
import com.redis.redis_cache.mapper.UserMapper;
import com.redis.redis_cache.repository.UserRepository;
import com.redis.redis_cache.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
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
    UserMapper userMapper;
    PageMapper pageMapper;
    ApplicationEventPublisher eventPublisher;

    @Override
    @Cacheable(value = CacheConst.CACHE_USER_DETAIL, key = "#id")
    public UserResponse getUserById(Long id) {
        // Giả lập độ trễ DB cho mục đích DEMO ONLY
        simulateDbLatency();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        log.info("Fetched user from DB (cache miss): {}", id);
        return userMapper.entityToResponse(user);
    }

    @Override
    @CachePut(value = CacheConst.CACHE_USER_DETAIL, key = "#result.id", condition = "#result != null")
    public UserResponse saveOrUpdateUser(UserRequest user) {
        User savedUser = userRepository.save(userMapper.requestToEntity(user));
        UserResponse response = userMapper.entityToResponse(savedUser);
        
        // Publish event để xóa cache list có liên quan
        eventPublisher.publishEvent(new UserSavedEvent(this, response));
        
        log.info("User saved/updated và cache đã được refresh: {}", response.getId());
        return response;
    }

    @Override
    @Cacheable(value = CacheConst.CACHE_USER_LIST, key = "'search:' + #keyword + '-page:' + #page + '-size:' + #size")
    public PageResponse<UserResponse> searchUsers(String keyword, int page, int size) {
        // Giả lập độ trễ DB cho mục đích DEMO ONLY
        simulateDbLatency();

        Pageable pageable = PageRequest.of(page, size);
        Page<User> pageData = userRepository.findByUsernameContainingIgnoreCase(keyword, pageable);
        
        log.info("Search users from DB (cache miss): keyword={}, page={}, size={}", keyword, page, size);
        return pageMapper.pageToResponse(pageData, page, size);
    }


    private void simulateDbLatency() {
        try {
            Thread.sleep(3000); //3 giây
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    @CacheEvict(value = CacheConst.CACHE_USER_DETAIL, key = "#id")
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        userRepository.delete(user);
        log.info("User deleted and cache evicted: {}", id);
        
        // Publish event để xóa cache search có liên quan
        UserResponse response = userMapper.entityToResponse(user);
        eventPublisher.publishEvent(new UserSavedEvent(this, response));
    }
}
