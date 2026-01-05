package smu.nuda.support.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.domain.auth.jwt.JwtProvider;
import smu.nuda.domain.auth.jwt.TokenType;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.support.member.MemberTestFactory;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTestFactory {

    private final JwtProvider jwtProvider;
    private final MemberTestFactory memberFactory;

    public String activeAccessToken() {
        Member member = memberFactory.active();
        return jwtProvider.generateToken(
                member.getId(),
                member.getEmail(),
                member.getRole().name(),
                TokenType.ACCESS
        );
    }

    public String inactiveAccessToken() {
        Member member = memberFactory.inactive();
        return jwtProvider.generateToken(
                member.getId(),
                member.getEmail(),
                member.getRole().name(),
                TokenType.ACCESS
        );
    }

    public String expiredAccessToken() {
        Member member = memberFactory.active();

        return jwtProvider.generateToken(
                member.getId(),
                member.getEmail(),
                member.getRole().name(),
                TokenType.ACCESS,
                new Date(System.currentTimeMillis() - 1000)
        );
    }

    public String validRefreshToken() {
        Member member = memberFactory.active();
        return jwtProvider.generateToken(
                member.getId(),
                member.getEmail(),
                member.getRole().name(),
                TokenType.REFRESH
        );
    }
}
