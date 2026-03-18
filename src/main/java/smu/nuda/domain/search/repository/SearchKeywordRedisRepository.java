package smu.nuda.domain.search.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import smu.nuda.global.cache.CacheKeyFactory;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class SearchKeywordRedisRepository {

    private final StringRedisTemplate stringRedisTemplate;
    private final CacheKeyFactory cacheKeyFactory;
    private final Clock clock;

    private static final Duration TTL = Duration.ofDays(14);

    @Async("eventExecutor")
    public void increment(String keyword) {
        String key = currentWeekKey();
        stringRedisTemplate.opsForZSet().incrementScore(key, keyword, 1);
        stringRedisTemplate.expire(key, TTL);
    }

    public List<String> getTopKeywords(int n) {
        if (n <= 0) return List.of();

        String key = currentWeekKey();
        Set<String> result = stringRedisTemplate.opsForZSet().reverseRange(key, 0, n - 1);
        if (result == null) return List.of();

        return List.copyOf(result);
    }

    private String currentWeekKey() {
        LocalDate today = LocalDate.now(clock);
        String weekKey = String.format("%d-W%02d",
                today.get(WeekFields.ISO.weekBasedYear()),
                today.get(WeekFields.ISO.weekOfWeekBasedYear())
        );
        return cacheKeyFactory.searchKeywordRanking(weekKey);
    }
}
