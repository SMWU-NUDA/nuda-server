package smu.nuda.domain.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.global.error.DomainException;
import smu.nuda.global.security.exception.JwtAuthenticationException;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;


    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
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

    public String generateToken(Long memberId, String email, String role, TokenType tokenType, Date expiration) {
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .claim("email", email)
                .claim("role", role)
                .claim("tokenType", tokenType.name())
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractMemberId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    public TokenType extractTokenType(String token) {
        return TokenType.valueOf(
                parseClaims(token).get("tokenType", String.class)
        );
    }

    public void validateTokenTypeOrThrow(String token, TokenType expected) {
        TokenType actual = extractTokenType(token);
        if (actual != expected) throw new DomainException(AuthErrorCode.INVALID_TOKEN_TYPE);
    }

}
