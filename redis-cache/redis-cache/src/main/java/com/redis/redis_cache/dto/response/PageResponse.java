package com.redis.redis_cache.dto.response;
import lombok.*;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor // Quan trọng: Giúp Redis đọc ngược từ JSON thành Object
@AllArgsConstructor
public class PageResponse<T> implements Serializable {
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private List<T> content;
}