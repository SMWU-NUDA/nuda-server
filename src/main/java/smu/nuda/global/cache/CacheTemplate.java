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
        if (type.isInstance(cached)) {
            log.info("CACHE HIT key={}", key);
            return type.cast(cached);
        }

        T value = supplier.get();
        if (value != null) jsonRedisTemplate.opsForValue().set(key, value, ttl);
        log.info("CACHE Miss key={}", key);
        log.info("cached class={}", cached == null ? null : cached.getClass());
        return value;
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