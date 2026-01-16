package smu.nuda.domain.signupdraft.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.signupdraft.repository.SignupDraftRepository;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SignupDraftService {

    private final SignupDraftRepository signupDraftRepository;
    private final Clock clock;

    @Transactional
    public int expireDrafts() {
        LocalDateTime now = LocalDateTime.now(clock);
        return signupDraftRepository.deleteExpired(now);
    }
}
