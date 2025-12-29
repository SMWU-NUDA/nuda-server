package smu.nuda.domain.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.member.entity.enums.Status;
import smu.nuda.domain.member.error.MemberErrorCode;
import smu.nuda.domain.member.repository.MemberRepository;
import smu.nuda.global.error.DomainException;
import smu.nuda.global.security.CustomUserDetails;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final MemberRepository memberRepository;
    private final JwtProperties jwtProperties;


    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(JwtConstants.AUTH_HEADER);
        if (bearerToken != null && bearerToken.startsWith(JwtConstants.TOKEN_PREFIX)) {
            return bearerToken.substring(JwtConstants.TOKEN_PREFIX.length());
        }
        return null;
    }

    public String generateToken(Long memberId, String email, String role, TokenType tokenType) {
        long expiration = jwtProperties.getExpiration(tokenType);

        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .claim("email", email)
                .claim("role", role)
                .claim("tokenType", tokenType.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseClaimsOrThrow(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            throw new DomainException(AuthErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    public void validateAccessTokenOrThrow(String token) {
        try {
            Claims claims = parseClaimsOrThrow(token);

            if (!TokenType.ACCESS.name().equals(claims.get("tokenType", String.class))) {
                throw new DomainException(AuthErrorCode.INVALID_ACCESS_TOKEN);
            }

        } catch (ExpiredJwtException e) {
            throw new DomainException(AuthErrorCode.EXPIRED_ACCESS_TOKEN);
        }
    }

    public void validateSignupTokenOrThrow(String token) {
        try {
            Claims claims = parseClaimsOrThrow(token);

            if (!TokenType.SIGNUP.name()
                    .equals(claims.get("tokenType", String.class))) {
                throw new DomainException(AuthErrorCode.INVALID_SIGNUP_TOKEN);
            }

        } catch (ExpiredJwtException e) {
            throw new DomainException(AuthErrorCode.EXPIRED_SIGNUP_TOKEN);
        }
    }

    public Authentication getAuthentication(String token) {

        Claims claims = parseClaimsOrThrow(token);
        Long memberId = Long.valueOf(claims.getSubject());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new DomainException(MemberErrorCode.MEMBER_NOT_FOUND)
                );

        // 회원가입 완료 여부 필터에서 차단
        if (member.getStatus() != Status.ACTIVE) {
            throw new DomainException(AuthErrorCode.MEMBER_NOT_ACTIVE);
        }

        CustomUserDetails principal = new CustomUserDetails(member);

        return new UsernamePasswordAuthenticationToken(
                principal,
                token,
                principal.getAuthorities()
        );
    }

    public Authentication extractAuthentication(HttpServletRequest request) {

        String accessToken = resolveToken(request);

        if (accessToken == null) {
            throw new DomainException(AuthErrorCode.INVALID_ACCESS_TOKEN);
        }

        validateAccessTokenOrThrow(accessToken);

        return getAuthentication(accessToken);
    }



}
