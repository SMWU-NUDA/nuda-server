package smu.nuda.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.domain.auth.redis.EmailAuthCacheRepository;
import smu.nuda.domain.auth.util.VerificationCodeGenerator;
import smu.nuda.domain.member.repository.MemberRepository;
import smu.nuda.global.error.DomainException;
import smu.nuda.global.mail.EmailService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailAuthCacheRepository emailAuthCacheRepository;
    private final MemberRepository memberRepository;
    private final VerificationCodeGenerator codeGenerator;
    private final EmailService emailService;

    public Boolean requestVerificationCode(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DomainException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String code = codeGenerator.generate();
        emailAuthCacheRepository.saveCode(email, code);
        emailService.send(
                email,
                "[Nuda] 이메일 인증번호",
                "인증번호는 " + code + " 입니다.\n5분 이내에 입력해주세요."
        );
        return true;
    }
}
