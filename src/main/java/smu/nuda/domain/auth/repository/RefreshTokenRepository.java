package smu.nuda.domain.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final StringRedisTemplate redisTemplate;

    private String refreshKey(Long memberId) { return "auth:refresh:token:" + memberId; }

    public void save(Long memberId, String refreshToken, long ttlMillis) {
        redisTemplate.opsForValue()
                .set(refreshKey(memberId), refreshToken, ttlMillis, TimeUnit.MILLISECONDS);
    }

    public String find(Long memberId) {
        return redisTemplate.opsForValue().get(refreshKey(memberId));
    }

    public void delete(Long memberId) {
        redisTemplate.delete(refreshKey(memberId));
    }

}
