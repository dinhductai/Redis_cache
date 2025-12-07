package com.redis.redis_cache.service.Impl;

import com.redis.redis_cache.service.CacheMonitorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class CacheMonitorServiceImpl implements CacheMonitorService {
    CacheManager cacheManager;
    RedisConnectionFactory redisConnectionFactory;
    @Override
    public Map<String, Object> getAllCacheStats() {
        Map<String, Object> cacheDetails = cacheManager.getCacheNames().stream()
                .map(this::getCacheInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        info -> (String) info.get("name"),
                        info -> info
                ));

        Map<String, Object> response = new HashMap<>();
        response.put("caches", cacheDetails);
        response.put("totalCaches", cacheDetails.size());

        return response;
    }

    @Override
    public Map<String, Object> clearAllCaches() {
        long clearedCount = cacheManager.getCacheNames().stream()
                .map(cacheManager::getCache)
                .filter(Objects::nonNull)
                .peek(Cache::clear)
                .count();

        log.warn("Đã clear {} cache layers theo yêu cầu thủ công.", clearedCount);

        return Map.of(
                "message", String.format("Cleared %d caches successfully", clearedCount),
                "warning", "This action impacts performance temporarily."
        );
    }

    @Override
    public Map<String, String> getHealthStatus() {
        try {
            var connection = redisConnectionFactory.getConnection();
            String pingResult = connection.ping();
            connection.close(); // đóng connection sau khi dùng

            return Map.of(
                    "status", "UP",
                    "redis", "Connected",
                    "ping", String.valueOf(pingResult)
            );
        } catch (Exception e) {
            log.error("Redis health check failed", e);
            return Map.of(
                    "status", "DOWN",
                    "redis", "Connection failed: " + e.getMessage()
            );
        }
    }

    private Map<String, Object> getCacheInfo(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) return null;

        Map<String, Object> info = new HashMap<>();
        info.put("name", cacheName);

        Object nativeCache = cache.getNativeCache();

        if (nativeCache instanceof RedisCache redisCache) {
            info.put("type", "Redis");
            // Lấy prefix thực tế
            info.put("prefix", redisCache.getCacheConfiguration().getKeyPrefixFor(cacheName));
            // Lấy TTL thực tế từ config
            info.put("ttl", redisCache.getCacheConfiguration().getTtl().toSeconds() + " seconds");
        } else {
            info.put("type", "In-Memory/Generic");
        }

        return info;
    }
}
