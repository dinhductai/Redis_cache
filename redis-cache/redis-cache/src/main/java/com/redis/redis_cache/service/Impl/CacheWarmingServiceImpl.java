package com.redis.redis_cache.service.Impl;

import com.redis.redis_cache.constant.CacheConst;
import com.redis.redis_cache.dto.response.UserResponse;
import com.redis.redis_cache.entity.User;
import com.redis.redis_cache.mapper.UserMapper;
import com.redis.redis_cache.repository.UserRepository;
import com.redis.redis_cache.service.CacheWarmingService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class CacheWarmingServiceImpl implements CacheWarmingService {

    UserRepository userRepository;
    UserMapper userMapper;
    CacheManager cacheManager;

    //giới hạn số lượng user load lên cache mặc định để tránh quá tải RAM
    private static final int DEFAULT_WARMUP_LIMIT = 1000;

    @Override
    @Async // chạy trên thread riêng, không chặn quá trình khởi động app
    @EventListener(ApplicationReadyEvent.class)
    public void warmUpCache() {
        log.info("bắt đầu quy trình Cache Warming tự động");
        //mặc định load 1000 user mới nhất
        warmUpTopUsers(DEFAULT_WARMUP_LIMIT);
    }

    @Override
    public void manualWarmUp() {
        log.info("kích hoạt Manual Cache Warming...");
        warmUpTopUsers(DEFAULT_WARMUP_LIMIT);
    }

    @Override
    public void warmUpTopUsers(int limit) {
        long startTime = System.currentTimeMillis();
        try {
            // dùng PageRequest để DB chỉ trả về đúng số lượng cần thiết
            PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "id"));
            Page<User> page = userRepository.findAll(pageRequest);
            List<User> topUsers = page.getContent();
            if (topUsers.isEmpty()) {
                log.info("database trống, không có dữ liệu để warm up.");
                return;
            }
            var userCache = cacheManager.getCache(CacheConst.CACHE_USER_DETAIL);
            if (userCache != null) {
                int count = 0;
                for (User user : topUsers) {
                    UserResponse response = userMapper.entityToResponse(user);
                    userCache.put(user.getId(), response);
                    count++;
                }
                long duration = System.currentTimeMillis() - startTime;
                log.info("cache warming hoàn tất: Đã load {} users mới nhất vào cache trong {}ms", count, duration);
            } else {
                log.error("Không tìm thấy cache với tên: {}", CacheConst.CACHE_USER_DETAIL);
            }
        } catch (Exception e) {
            log.error("Cache Warming thất bại: {}", e.getMessage(), e);
        }
    }
}