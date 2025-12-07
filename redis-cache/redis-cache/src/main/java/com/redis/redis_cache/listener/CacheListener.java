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

        // xóa cache chi tiết
        if (event.getUser().getId() != null) {
            Objects.requireNonNull(cacheManager.getCache(CacheConst.CACHE_USER_DETAIL))
                    .evict(event.getUser().getId());
        }

        //xóa sạch cache list
        Objects.requireNonNull(cacheManager.getCache(CacheConst.CACHE_USER_LIST))
                .clear();
    }
}