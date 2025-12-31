package smu.nuda.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmail(String email);
    Optional<Member> findByUsername(String username);
    boolean existsByNickname(String nickname);
    boolean existsByUsername(String username);
}
