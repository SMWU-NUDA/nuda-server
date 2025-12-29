package smu.nuda.domain.auth.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class EmailAuthCacheRepository {

    private static final int MAX_ATTEMPTS = 5;

    private final StringRedisTemplate redisTemplate;

    private String codeKey(String email) {
        return "auth:email:code:" + email;
    }
    private String verifiedKey(String email) {
        return "auth:email:verified:" + email;
    }
    private String attemptKey(String email) { return "auth:email:attempt:" + email; }

    public void saveCode(String email, String code) {
        redisTemplate.opsForValue()
                .set(codeKey(email), code, Duration.ofMinutes(5));
    }

    public String getCode(String email) {
        return redisTemplate.opsForValue().get(codeKey(email));
    }

    public void markVerified(String email) {
        redisTemplate.opsForValue()
                .set(verifiedKey(email), "true", Duration.ofMinutes(10));
    }

    public boolean isVerified(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(verifiedKey(email)));
    }

    public void clear(String email) {
        redisTemplate.delete(codeKey(email));
        redisTemplate.delete(attemptKey(email));
    }

    public void clearVerified(String email) {
        redisTemplate.delete(verifiedKey(email));
    }

    public int increaseAttempt(String email) {
        Long count = redisTemplate.opsForValue().increment(attemptKey(email));
        if (count != null && count == 1L) {
            // 첫 실패 시 TTL 설정
            redisTemplate.expire(attemptKey(email), Duration.ofMinutes(5));
        }
        return count == null ? 0 : count.intValue();
    }

    public boolean isAttemptExceeded(String email) {
        String value = redisTemplate.opsForValue().get(attemptKey(email));
        return value != null && Integer.parseInt(value) >= MAX_ATTEMPTS;
    }
}

