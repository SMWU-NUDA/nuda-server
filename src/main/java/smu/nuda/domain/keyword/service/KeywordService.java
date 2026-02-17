package smu.nuda.domain.keyword.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.keyword.dto.KeywordResponse;
import smu.nuda.domain.keyword.entity.Keyword;
import smu.nuda.domain.keyword.error.KeywordErrorCode;
import smu.nuda.domain.keyword.repository.KeywordRepository;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.global.error.DomainException;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;

    @Transactional(readOnly = true)
    public KeywordResponse getMyKeyword(Member member) {
        Keyword keyword = keywordRepository.findByMember(member)
                .orElseThrow(() -> new DomainException(KeywordErrorCode.KEYWORD_NOT_FOUND));
        return KeywordResponse.from(keyword);
    }

}
