package smu.nuda.domain.keyword.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.keyword.entity.Keyword;

import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByMember(Member member);
}
