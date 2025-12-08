//package com.redis.redis_cache.controller;
//
//import com.redis.redis_cache.service.CacheWarmingService;
//import com.redis.redis_cache.service.Impl.CacheWarmingServiceImpl;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/cache/warming")
//@RequiredArgsConstructor
//@Slf4j
//public class CacheWarmingController {
//
//    private final CacheWarmingService cacheWarmingService;
//    //logic xu ly tam thoi
//    @PostMapping("/trigger")
//    public ResponseEntity<Map<String, String>> triggerWarmUp() {
//        try {
//            cacheWarmingService.manualWarmUp();
//            Map<String, String> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Cache warming completed successfully");
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, String> response = new HashMap<>();
//            response.put("status", "error");
//            response.put("message", "Cache warming failed: " + e.getMessage());
//            return ResponseEntity.status(500).body(response);
//        }
//    }
//
//    @PostMapping("/top-users")
//    public ResponseEntity<Map<String, String>> warmUpTopUsers(
//            @RequestParam(defaultValue = "100") int limit) {
//        try {
//            cacheWarmingService.warmUpTopUsers(limit);
//            Map<String, String> response = new HashMap<>();
//            response.put("status", "success");
//            response.put("message", "Warmed cache for top " + limit + " users");
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, String> response = new HashMap<>();
//            response.put("status", "error");
//            response.put("message", "Failed: " + e.getMessage());
//            return ResponseEntity.status(500).body(response);
//        }
//    }
//}
