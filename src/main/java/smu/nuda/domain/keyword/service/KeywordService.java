package smu.nuda.domain.keyword.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.keyword.dto.KeywordRequest;
import smu.nuda.domain.keyword.dto.KeywordResponse;
import smu.nuda.domain.keyword.entity.Keyword;
import smu.nuda.domain.keyword.error.KeywordErrorCode;
import smu.nuda.domain.keyword.repository.KeywordRepository;
import smu.nuda.global.error.DomainException;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;

    @Transactional(readOnly = true)
    public KeywordResponse getMyKeyword(Long memberId) {
        Keyword keyword = keywordRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DomainException(KeywordErrorCode.KEYWORD_NOT_FOUND));
        return KeywordResponse.from(keyword);
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

        return KeywordResponse.from(keyword);
    }


}
