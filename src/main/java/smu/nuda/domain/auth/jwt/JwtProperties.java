package smu.nuda.domain.auth.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.global.error.DomainException;

import java.util.EnumMap;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;

    private Map<TokenType, TokenProperty> tokens = new EnumMap<>(TokenType.class);

    public long getExpiration(TokenType tokenType) {
        TokenProperty property = tokens.get(tokenType);
        if (property == null) {
            throw new DomainException(AuthErrorCode.JWT_CONFIGURATION_NOT_FOUND);
        }
        return property.getExpiration();
    }

    @Getter
    @Setter
    public static class TokenProperty {
        private long expiration;
    }
}
