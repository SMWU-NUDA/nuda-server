package smu.nuda.global.ml.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import smu.nuda.domain.product.dto.enums.ProductKeywordType;
import smu.nuda.global.cache.facade.MlProductCacheFacade;

@Component
@RequiredArgsConstructor
@Slf4j
public class WarmUpService {

    private final MlProductCacheFacade rankingCacheFacade;

    @EventListener(ApplicationReadyEvent.class)
    public void warmUpRankingCache() {

        log.info("[ML] Global Ranking Warm-Up started");

        for (ProductKeywordType keyword : ProductKeywordType.values()) {
            try {
                rankingCacheFacade.getGlobalRanking(keyword);
                log.info("WarmUp 성공: {}", keyword);
            } catch (Exception e) {
                log.warn("WarmUp 실패: {}, 사유: {}", keyword, e.getMessage(), e);
            }
        }

        log.info("[ML] Global Ranking Warm-Up ended");
    }
}
