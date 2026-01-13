package smu.nuda.domain.signupdraft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.nuda.domain.signupdraft.entity.SignupDraft;

import java.util.Optional;

public interface SignupDraftRepository extends JpaRepository<SignupDraft, Long> {
    Optional<SignupDraft> findBySignupToken(String signupToken);
    boolean existsBySignupToken(String signupToken);
}
