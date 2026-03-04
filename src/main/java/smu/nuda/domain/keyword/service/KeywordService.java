package smu.nuda.domain.keyword.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.keyword.dto.KeywordRequest;
import smu.nuda.domain.keyword.dto.KeywordResponse;
import smu.nuda.domain.keyword.entity.Keyword;
import smu.nuda.domain.keyword.error.KeywordErrorCode;
import smu.nuda.domain.keyword.event.KeywordUpdateEvent;
import smu.nuda.domain.keyword.repository.KeywordRepository;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.global.error.DomainException;
import smu.nuda.global.ml.dto.KeywordSyncRequest;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public KeywordResponse getMyKeyword(Member member) {
        Keyword keyword = keywordRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new DomainException(KeywordErrorCode.KEYWORD_NOT_FOUND));
        return KeywordResponse.of(keyword, member);
    }

    @Transactional
    public KeywordResponse updateMyKeyword(Long memberId, KeywordRequest request) {
        Keyword keyword = keywordRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DomainException(KeywordErrorCode.KEYWORD_NOT_FOUND));

        keyword.update(
                request.getIrritationLevel(),
                request.getScent(),
                request.getAdhesion(),
                request.getThickness()
        );

        KeywordSyncRequest payload = new KeywordSyncRequest(
                memberId,
                keyword.getIrritationLevel(),
                keyword.getScent(),
                keyword.getThickness(),
                keyword.getAdhesion()
        );
        eventPublisher.publishEvent(new KeywordUpdateEvent(payload));

        return KeywordResponse.from(keyword);
    }


}
