package smu.nuda.global.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheTemplate {

    private final RedisTemplate<String, Object> jsonRedisTemplate;

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Duration ttl, Supplier<T> supplier) {
        Object cached = jsonRedisTemplate.opsForValue().get(key);
        if (cached != null) return (T) cached;

        T value = supplier.get();
        if (value != null) jsonRedisTemplate.opsForValue().set(key, value, ttl);

        return value;
    }

    public <T> T get(String key, Duration ttl, Supplier<T> supplier, Class<T> type) {
        Object cached = jsonRedisTemplate.opsForValue().get(key);
        if (type.isInstance(cached)) return type.cast(cached);

        T value = supplier.get();
        if (value != null) jsonRedisTemplate.opsForValue().set(key, value, ttl);

        return value;
    }

    public <T> T getWithFallback(String key, Duration ttl, Supplier<T> supplier, Supplier<T> defaultSupplier, Class<T> type) {
        Object cached = jsonRedisTemplate.opsForValue().get(key);

        // 캐시 HIT
        if (type.isInstance(cached)) {
            log.info("CACHE HIT key={}", key);
            return type.cast(cached);
        }

        // 캐시 MISS → ML 시도
        try {
            T value = supplier.get();
            if (value != null) {
                jsonRedisTemplate.opsForValue().set(key, value, ttl);
            }
            log.info("CACHE MISS → ML SUCCESS key={}", key);
            return value;
        } catch (Exception e) {
            log.warn(
                    "ML FAILED key={} → DEFAULT USED cause={} message={}",
                    key,
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
            return defaultSupplier.get();
        }
    }

    public void put(String key, Object value, Duration ttl) {
        if (value != null) jsonRedisTemplate.opsForValue().set(key, value, ttl);
    }

    public void evict(String key) {
        jsonRedisTemplate.delete(key);
    }

    public void evictByPrefix(String prefix) {
        jsonRedisTemplate.keys(prefix + "*").forEach(jsonRedisTemplate::delete);
    }
}