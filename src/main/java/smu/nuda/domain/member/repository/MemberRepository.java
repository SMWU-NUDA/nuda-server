package smu.nuda.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByEmailAndStatusNot(String email, Status status);
    Optional<Member> findByUsername(String username);
    boolean existsByNicknameAndStatusNot(String nickname, Status status);
    boolean existsByUsernameAndStatusNot(String username, Status status);
    List<Member> findAllByStatusAndDeletedAtBefore(Status status, LocalDateTime threshold);
}
