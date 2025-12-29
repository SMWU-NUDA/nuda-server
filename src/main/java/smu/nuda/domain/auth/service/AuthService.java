package smu.nuda.domain.auth.service;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.auth.dto.*;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.domain.auth.jwt.JwtProperties;
import smu.nuda.domain.auth.jwt.JwtProvider;
import smu.nuda.domain.auth.jwt.TokenType;
import smu.nuda.domain.auth.repository.EmailAuthRepository;
import smu.nuda.domain.auth.repository.RefreshTokenRepository;
import smu.nuda.domain.auth.util.VerificationCodeGenerator;
import smu.nuda.domain.member.dto.DeliveryRequest;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.Role;
import smu.nuda.domain.member.entity.enums.Status;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.domain.member.repository.MemberRepository;
import smu.nuda.global.error.DomainException;
import smu.nuda.global.mail.EmailService;
import smu.nuda.global.response.ApiResponse;
import smu.nuda.global.security.CustomUserDetails;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmailAuthRepository emailAuthRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;
    private final VerificationCodeGenerator codeGenerator;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;

    public void requestVerificationCode(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new DomainException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String code = codeGenerator.generate();
        emailAuthRepository.saveCode(email, code);
        emailService.send(
                email,
                "[Nuda] 이메일 인증번호",
                "인증번호는 " + code + " 입니다.\n5분 이내에 입력해주세요."
        );
    }

    public Boolean verifyEmailCode(String email, String inputCode) {
        // 중복 인증 요청 허용
        if (emailAuthRepository.isVerified(email)) {
            return true;
        }

        // 중복 인증 요청 횟수 초과
        if (emailAuthRepository.isAttemptExceeded(email)) {
            throw new DomainException(AuthErrorCode.EMAIL_VERIFICATION_TOO_MANY_ATTEMPTS);
        }

        // Redis에서 저장된 인증번호 조회
        String savedCode = emailAuthRepository.getCode(email);
        if (savedCode == null) {
            throw new DomainException(AuthErrorCode.EMAIL_VERIFICATION_EXPIRED);
        }

        // 인증번호 불일치
        if (!savedCode.equals(inputCode)) {
            emailAuthRepository.increaseAttempt(email);
            throw new DomainException(AuthErrorCode.EMAIL_VERIFICATION_MISMATCH);
        }

        emailAuthRepository.markVerified(email);
        emailAuthRepository.clear(email);

        return true;
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        String email = request.getEmail();
        if (!emailAuthRepository.isVerified(email)) {
            throw new DomainException(AuthErrorCode.EMAIL_NOT_VERIFIED);
        }

        Member member = Member.builder()
                .email(email)
                .username(request.getUsername())
                .nickname(request.getNickname())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .status(Status.SIGNUP_IN_PROGRESS) // 회원가입 단계 진입
                .build();

        memberRepository.save(member);
        emailAuthRepository.clearVerified(email);

        String signupToken = jwtProvider.generateToken(
                member.getId(),
                member.getEmail(),
                null,
                TokenType.SIGNUP
        );

        return new SignupResponse(signupToken);
    }

    @Transactional
    public void updateDelivery(DeliveryRequest request, Member authMember) {
        if (authMember.getStatus() != Status.SIGNUP_IN_PROGRESS) {
            throw new DomainException(AuthErrorCode.INVALID_SIGNUP_FLOW);
        }

        Member member = memberRepository.findById(authMember.getId())
                .orElseThrow(() -> new DomainException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.updateDelivery(
                request.getRecipient(),
                request.getPhoneNum(),
                request.getPostalCode(),
                request.getAddress1(),
                request.getAddress2()
        );
    }

    @Transactional
    public void completeSignup(Member authMember) {
        if (authMember.getStatus() != Status.SIGNUP_IN_PROGRESS) {
            throw new DomainException(AuthErrorCode.INVALID_SIGNUP_FLOW);
        }
        Member member = memberRepository.findById(authMember.getId())
                .orElseThrow(() -> new DomainException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.activate();
    }


    public LoginResponse login(LoginRequest request) {
        Member member = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new DomainException(AuthErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new DomainException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        if (member.getStatus() != Status.ACTIVE) {
            throw new DomainException(AuthErrorCode.MEMBER_NOT_ACTIVE);
        }

        String accessToken = jwtProvider.generateToken(
                member.getId(),
                member.getEmail(),
                member.getRole().name(),
                TokenType.ACCESS
        );
        String refreshToken = jwtProvider.generateToken(
                member.getId(),
                null,
                null,
                TokenType.REFRESH
        );
        refreshTokenRepository.save(
                member.getId(),
                refreshToken,
                jwtProperties.getExpiration(TokenType.REFRESH)
        );

        return new LoginResponse(accessToken, refreshToken);
    }

    public ReissueResponse reissue(String refreshToken) {
        jwtProvider.validateRefreshTokenOrThrow(refreshToken);
        Claims claims = jwtProvider.parseClaimsOrThrow(refreshToken);
        Long memberId = Long.valueOf(claims.getSubject());

        String savedToken = refreshTokenRepository.find(memberId);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new DomainException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        refreshTokenRepository.delete(memberId);
        String newRefreshToken = jwtProvider.generateToken(
                memberId,
                null,
                null,
                TokenType.REFRESH
        );
        refreshTokenRepository.save(
                memberId,
                newRefreshToken,
                jwtProperties.getExpiration(TokenType.REFRESH)
        );

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new DomainException(MemberErrorCode.MEMBER_NOT_FOUND));

        String newAccessToken = jwtProvider.generateToken(
                member.getId(),
                member.getEmail(),
                member.getRole().name(),
                TokenType.ACCESS
        );

        return new ReissueResponse(newAccessToken, newRefreshToken);
    }

    public void logout(Member member) {
        refreshTokenRepository.delete(member.getId());
    }

    public void checkNickname(String nickname) {
        boolean exists = memberRepository.existsByNickname(nickname);
        if (exists) {
            throw new DomainException(AuthErrorCode.NICKNAME_DUPLICATED, nickname);
        }
    }

    public void checkUsername(String username) {
        boolean exists = memberRepository.existsByUsername(username);
        if (exists) {
            throw new DomainException(AuthErrorCode.USERNAME_DUPLICATED, username);
        }
    }

}
