package smu.nuda.domain.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.domain.member.repository.MemberRepository;
import smu.nuda.global.error.DomainException;

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

    public Claims parseClaimsOrThrow(String token) {
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

    public Long extractMemberId(String token) {
        return Long.valueOf(parseClaimsOrThrow(token).getSubject());
    }

    public TokenType extractTokenType(String token) {
        return TokenType.valueOf(
                parseClaimsOrThrow(token).get("tokenType", String.class)
        );
    }

    public void validateTokenTypeOrThrow(String token, TokenType expected) {
        try {
            TokenType actual = extractTokenType(token);
            if (actual != expected) {
                throw new DomainException(AuthErrorCode.INVALID_TOKEN_TYPE);
            }
        } catch (ExpiredJwtException e) {
            throw new DomainException(AuthErrorCode.EXPIRED_TOKEN);
        }
    }

}
