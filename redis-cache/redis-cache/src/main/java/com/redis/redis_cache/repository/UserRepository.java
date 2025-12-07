package com.redis.redis_cache.repository;

import com.redis.redis_cache.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
