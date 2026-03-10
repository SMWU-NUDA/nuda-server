package smu.nuda.global.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import smu.nuda.global.error.MlErrorCode;
import smu.nuda.global.ml.exception.MlApiException;

import java.time.Duration;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheTemplate {

    private final RedisTemplate<String, Object> jsonRedisTemplate;
    private final ObjectMapper objectMapper;

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
        if (cached != null) {
            log.info("CACHE HIT key={}", key);
            return objectMapper.convertValue(cached, type);
        }

        log.info("CACHE Miss key={}", key);

        T value = supplier.get();
        if (value != null) jsonRedisTemplate.opsForValue().set(key, value, ttl);

        return value;
    }

    public <T> T getWithFallback(String key, Duration ttl, Duration fallbackTtl, Supplier<T> supplier, Supplier<T> defaultSupplier, Class<T> type) {
        Object cached = jsonRedisTemplate.opsForValue().get(key);

        // 캐시 HIT
        if (cached != null) {
            log.info("CACHE HIT key={}", key);
            return objectMapper.convertValue(cached, type);
        }

        // 캐시 MISS
        log.info("CACHE MISS key={}", key);

        try {
            // ML 호출 시도
            T value = supplier.get();
            if (value != null) {
                jsonRedisTemplate.opsForValue().set(key, value, ttl);
                log.info("ML SUCCESS key={}", key);
                return value;
            }

            log.warn("ML RETURNED NULL key={} → DEFAULT USED", key);
        } catch (MlApiException e) {

            // 리뷰 부족은 비즈니스 예외 → 그대로 전파
            if (e.getErrorCode() == MlErrorCode.REVIEW_INSUFFICIENT) throw e;

            log.warn(
                    "ML FAILED key={} → DEFAULT USED cause={} message={}",
                    key,
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
        } catch (Exception e) {
            log.warn(
                    "ML FAILED key={} → DEFAULT USED cause={} message={}",
                    key,
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
        }

        // null || exception일 경우 fallback 처리
        T fallback = defaultSupplier.get();
        if (fallback != null) jsonRedisTemplate.opsForValue().set(key, fallback, fallbackTtl);

        return fallback;
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