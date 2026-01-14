package smu.nuda.domain.signupdraft.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.signupdraft.repository.SignupDraftRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SignupDraftService {

    private final SignupDraftRepository signupDraftRepository;

    @Transactional
    public int expireDrafts() {
        LocalDateTime now = LocalDateTime.now();
        return signupDraftRepository.deleteExpired(now);
    }
}
