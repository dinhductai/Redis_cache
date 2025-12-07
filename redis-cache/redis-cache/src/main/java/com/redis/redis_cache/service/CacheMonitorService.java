package com.redis.redis_cache.service;

import java.util.Map;

public interface CacheMonitorService {
    Map<String, Object> getAllCacheStats();
    Map<String, Object> clearAllCaches();
    Map<String, String> getHealthStatus();
}
