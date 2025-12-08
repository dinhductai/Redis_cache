//package com.redis.redis_cache.service.Impl;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.Set;
//import java.util.concurrent.TimeUnit;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class RedisService {
//
//    private final RedisTemplate<String, Object> redisTemplate;
//
//
//    public void evictCacheByPattern(String pattern) {
//        try {
//            Set<String> keys = redisTemplate.keys(pattern);
//            if (keys != null && !keys.isEmpty()) {
//                redisTemplate.delete(keys);
//                log.info("Evicted {} cache keys matching pattern: {}", keys.size(), pattern);
//            }
//        } catch (Exception e) {
//            log.error("Failed to evict cache by pattern: {}", pattern, e);
//        }
//    }
//
//    public void setWithTTL(String key, Object value, long timeout, TimeUnit unit) {
//        try {
//            redisTemplate.opsForValue().set(key, value, timeout, unit);
//            log.info("Set cache key: {} with TTL: {} {}", key, timeout, unit);
//        } catch (Exception e) {
//            log.error("Failed to set cache: {}", key, e);
//        }
//    }
//
//    public Object get(String key) {
//        try {
//            return redisTemplate.opsForValue().get(key);
//        } catch (Exception e) {
//            log.error("Failed to get cache: {}", key, e);
//            return null;
//        }
//    }
//
//
//    public boolean exists(String key) {
//        try {
//            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
//        } catch (Exception e) {
//            log.error("Failed to check key existence: {}", key, e);
//            return false;
//        }
//    }
//
//
//    public Long getTTL(String key) {
//        try {
//            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
//        } catch (Exception e) {
//            log.error("Failed to get TTL: {}", key, e);
//            return null;
//        }
//    }
//
//    public Long increment(String key) {
//        try {
//            return redisTemplate.opsForValue().increment(key);
//        } catch (Exception e) {
//            log.error("Failed to increment: {}", key, e);
//            return null;
//        }
//    }
//
//
//    public Boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
//        try {
//            return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
//        } catch (Exception e) {
//            log.error("Failed to setIfAbsent: {}", key, e);
//            return false;
//        }
//    }
//}
