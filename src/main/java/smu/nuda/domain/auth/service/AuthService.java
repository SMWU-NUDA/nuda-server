package smu.nuda.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.auth.dto.SignupRequest;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.domain.auth.redis.EmailAuthCacheRepository;
import smu.nuda.domain.auth.util.VerificationCodeGenerator;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.Role;
import smu.nuda.domain.member.entity.enums.Status;
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
    private final PasswordEncoder passwordEncoder;

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

    public Boolean verifyEmailCode(String email, String inputCode) {
        // 중복 인증 요청 허용
        if (emailAuthCacheRepository.isVerified(email)) {
            return true;
        }

        // 중복 인증 요청 횟수 초과
        if (emailAuthCacheRepository.isAttemptExceeded(email)) {
            throw new DomainException(AuthErrorCode.EMAIL_VERIFICATION_TOO_MANY_ATTEMPTS);
        }

        // Redis에서 저장된 인증번호 조회
        String savedCode = emailAuthCacheRepository.getCode(email);
        if (savedCode == null) {
            throw new DomainException(AuthErrorCode.EMAIL_VERIFICATION_EXPIRED);
        }

        // 인증번호 불일치
        if (!savedCode.equals(inputCode)) {
            emailAuthCacheRepository.increaseAttempt(email);
            throw new DomainException(AuthErrorCode.EMAIL_VERIFICATION_MISMATCH);
        }

        emailAuthCacheRepository.markVerified(email);
        emailAuthCacheRepository.clear(email);

        return true;
    }

    @Transactional
    public void signup(SignupRequest request) {

        String email = request.getEmail();
        if (!emailAuthCacheRepository.isVerified(email)) {
            throw new DomainException(AuthErrorCode.EMAIL_NOT_VERIFIED);
        }

        Member member = Member.builder()
                .email(email)
                .username(request.getUsername())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .status(Status.SIGNUP_IN_PROGRESS)
                .build();

        memberRepository.save(member);
        emailAuthCacheRepository.clearVerified(email);
    }


}
