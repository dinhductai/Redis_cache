//package com.redis.redis_cache.service.Impl;
//
//import com.redis.redis_cache.dto.response.UserResponse;
//import com.redis.redis_cache.entity.User;
//import com.redis.redis_cache.mapper.UserMapper;
//import com.redis.redis_cache.repository.UserRepository;
//import com.redis.redis_cache.service.CacheWarmingService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.cache.CacheManager;
//import org.springframework.context.event.EventListener;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class CacheWarmingServiceImpl implements CacheWarmingService {
//
//    private final UserRepository userRepository;
//    private final UserMapper userMapper;
//    private final CacheManager cacheManager;
//
//    @Override
//    @EventListener(ApplicationReadyEvent.class)
//    public void warmUpCache() {
//        long startTime = System.currentTimeMillis();
//        int cachedCount = 0;
//        try {
//            //lấy tất cả users hoặc top N users
//            List<User> users = userRepository.findAll();
//            if (users.isEmpty()) {
//                log.warn("database rỗng, không có data để warm cache");
//                return;
//            }
//            // preload từng user vào cache
//            var userCache = cacheManager.getCache("users");
//
//            for (User user : users) {
//                if (userCache != null) {
//                    UserResponse response = userMapper.entityToResponse(user);
//                    userCache.put(user.getId(), response);
//                    cachedCount++;
//                }
//            }
//            long duration = System.currentTimeMillis() - startTime;
//            log.info("Cache Warming hoàn tất: {} users trong {}ms", cachedCount, duration);
//        } catch (Exception e) {
//            log.error("Cache Warming thất bại: {}", e.getMessage(), e);
//        }
//    }
//    @Override
//    public void manualWarmUp() {
//        log.info("Manual Cache Warming được kích hoạt");
//        warmUpCache();
//    }
//
//    @Override
//    public void warmUpTopUsers(int limit) {
//        try {
//            List<User> topUsers = userRepository.findAll()
//                .stream()
//                .limit(limit)
//                .toList();
//            var userCache = cacheManager.getCache("users");
//            int count = 0;
//            for (User user : topUsers) {
//                if (userCache != null) {
//                    UserResponse response = userMapper.entityToResponse(user);
//                    userCache.put(user.getId(), response);
//                    count++;
//                }
//            }
//            log.info("Đã warm cache cho {} hot users", count);
//        } catch (Exception e) {
//            log.error("Failed to warm top users: {}", e.getMessage());
//        }
//    }
//}
