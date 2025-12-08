package com.redis.redis_cache.controller;

import com.redis.redis_cache.service.CacheMonitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
public class CacheMonitorController {

    private final CacheMonitorService cacheMonitorService;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = cacheMonitorService.getAllCacheStats();
        stats.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(stats);
    }


    @GetMapping("/clear-all")
    public ResponseEntity<Map<String, Object>> clearAllCaches() {
        return ResponseEntity.ok(cacheMonitorService.clearAllCaches());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> checkRedisHealth() {
        return ResponseEntity.ok(cacheMonitorService.getHealthStatus());
    }
}