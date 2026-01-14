package smu.nuda.domain.signupdraft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import smu.nuda.domain.signupdraft.entity.SignupDraft;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SignupDraftRepository extends JpaRepository<SignupDraft, Long> {
    Optional<SignupDraft> findBySignupToken(String signupToken);
    boolean existsBySignupToken(String signupToken);

    @Modifying(clearAutomatically = true)
    @Query("""
        DELETE FROM SignupDraft d
        WHERE d.expiresAt < :now
    """)
    int deleteExpired(@Param("now") LocalDateTime now);
}
