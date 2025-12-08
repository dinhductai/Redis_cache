package com.redis.redis_cache.service;

public interface CacheWarmingService {
    void warmUpCache();
    void manualWarmUp();
    void warmUpTopUsers(int limit);
}
