package smu.nuda.global.security.handler;

import org.springframework.http.HttpStatus;
import smu.nuda.domain.auth.error.AuthErrorCode;
import smu.nuda.global.error.ErrorCode;

public class SecurityHttpStatusResolver {

    private SecurityHttpStatusResolver() {}

    public static HttpStatus resolve(ErrorCode errorCode) {

        // 인증 관련
        if (errorCode instanceof AuthErrorCode authErrorCode) {

            // 401 Unauthorized
            if (authErrorCode == AuthErrorCode.AUTH_REQUIRED
                    || authErrorCode == AuthErrorCode.INVALID_ACCESS_TOKEN
                    || authErrorCode == AuthErrorCode.EXPIRED_TOKEN
                    || authErrorCode == AuthErrorCode.INVALID_REFRESH_TOKEN
                    || authErrorCode == AuthErrorCode.INVALID_TOKEN_TYPE
                    || authErrorCode == AuthErrorCode.JWT_CONFIGURATION_NOT_FOUND) {

                return HttpStatus.UNAUTHORIZED;
            }

            // 403 Forbidden
            if (authErrorCode == AuthErrorCode.ACCOUNT_DISABLED
                    || authErrorCode == AuthErrorCode.MEMBER_NOT_ACTIVE) {

                return HttpStatus.FORBIDDEN;
            }
        }

        // 기본값
        return HttpStatus.BAD_REQUEST;
    }
}
