package smu.nuda.domain.auth.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class EmailAuthCacheRepository {

    private final StringRedisTemplate redisTemplate;

    public EmailAuthCacheRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String codeKey(String email) {
        return "auth:email:code:" + email;
    }

    private String verifiedKey(String email) {
        return "auth:email:verified:" + email;
    }

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
        redisTemplate.delete(verifiedKey(email));
    }
}

