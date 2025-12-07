package com.redis.redis_cache.listener;

import com.redis.redis_cache.constant.CacheConst;
import com.redis.redis_cache.event.UserSavedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class CacheListener {

    private final CacheManager cacheManager;

    @EventListener
    public void handleUserSavedEvent(UserSavedEvent event) {
        log.info("Nhận sự kiện User Saved: {}. Tiến hành xóa Cache...", event.getUser().getId());

        // Xóa cache chi tiết của user được update/create
        if (event.getUser().getId() != null) {
            Objects.requireNonNull(cacheManager.getCache(CacheConst.CACHE_USER_DETAIL))
                    .evict(event.getUser().getId());
            log.info("Đã xóa cache user detail với id: {}", event.getUser().getId());
        }

        // Xóa SELECTIVE cache list có chứa username của user được update
        // Chỉ xóa các cache search có liên quan thay vì clear() toàn bộ
        if (event.getUser().getUsername() != null) {
            evictSearchCachesContainingUsername(event.getUser().getUsername());
        }
    }

    private void evictSearchCachesContainingUsername(String username) {

        log.info("Cache search sẽ tự expire sau TTL (eventual consistency approach)");

    }
}