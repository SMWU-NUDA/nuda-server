# 보안 계층 공통 응답 포맷 통일

## 📋 배경

프로젝트 전반의 API 응답은 `ApiResponse` 기반 공통 포맷으로 통일하고 있었다.
프론트엔드도 `success`, `errorCode`, `message`를 기준으로 예외를 처리하는 구조였다.


---
## ⚠️ 문제 상황

인증 실패(401), 인가 실패(403)는 일반 도메인 예외와 처리 경로가 다르다.
Spring Security는 이 예외들을 컨트롤러까지 보내지 않고 필터 체인에서 종료하기 때문에, `@RestControllerAdvice`만으로는 공통 응답 포맷을 적용할 수 없었다.

그 결과 401/403 응답은 다른 API와 형식이 달랐고, 경우에 따라 상태코드만 내려가거나 기본 Security 응답으로 종료됐다.
프론트엔드 입장에서는 동일한 API 호출 실패임에도 인증·인가 실패만 별도 방식으로 처리해야 했다.

또한 상태코드만으로는 실패 원인을 충분히 구분하기 어려웠다.
예를 들어 토큰 만료, 유효하지 않은 토큰, 비활성 사용자 상태는 모두 인증 실패로 묶일 수 있지만, 실제 후속 처리 방식은 서로 다르다.


---
## 💡 해결 방향

인증·인가 실패도 일반 API 예외와 동일한 형식으로 응답하도록 하고, 이 책임은 **보안 계층에서 직접 처리**하도록 설계했다.

핵심 판단은 두 가지였다.

1. 인증·인가 실패는 비즈니스 예외가 아니므로 Security Layer에서 종료한다.
2. 401/403도 공통 응답 포맷과 의미 있는 `errorCode`를 포함해 프론트엔드가 정책적으로 분기할 수 있도록 한다.


---
## 🔧 구현

### 1. errorCode 전달 방식

`AuthenticationEntryPoint`는 인증 실패 시 호출되지만, 그 시점에는 **왜** 인증이 실패했는지를 직접 알 수 없다.
JWT 파싱 오류인지, 토큰 만료인지, 비활성 사용자인지를 구분하려면 필터 단계에서 이미 판단한 결과를 전달받아야 했다.

이를 위해 `JwtAuthenticationFilter`에서 예외를 잡은 시점에 **request attribute**로 에러 종류를 기록했다.

```java
// JwtAuthenticationFilter — 예외 종류를 attribute에 기록
} catch (ExpiredJwtException e) {
    request.setAttribute("exception", AuthErrorCode.EXPIRED_TOKEN);
} catch (JwtException e) {
    request.setAttribute("exception", AuthErrorCode.INVALID_TOKEN);
}
```

`AuthenticationEntryPoint`는 `commence()` 호출 시 이 attribute를 꺼내 errorCode를 결정했다.

```java
// CustomAuthenticationEntryPoint
@Override
public void commence(HttpServletRequest request, HttpServletResponse response,
                     AuthenticationException authException) throws IOException {
    ErrorCode errorCode = (ErrorCode) request.getAttribute("exception");
    if (errorCode == null) {
        errorCode = AuthErrorCode.UNAUTHORIZED;
    }
    sendErrorResponse(response, HttpStatus.UNAUTHORIZED, errorCode);
}
```

`INACTIVE_MEMBER`는 JWT 자체가 유효하더라도 DB에서 회원 상태를 조회해 비활성 상태임을 확인한 시점에 filter에서 직접 응답을 내보내도록 처리했다.

### 2. 에러코드 분류

| 상황 | HTTP | errorCode |
|------|------|-----------|
| 토큰 없음 / 형식 오류 | 401 | `UNAUTHORIZED` |
| 토큰 만료 | 401 | `EXPIRED_TOKEN` |
| 토큰 위변조 | 401 | `INVALID_TOKEN` |
| 비활성 회원 | 401 | `INACTIVE_MEMBER` |
| 권한 없음 | 403 | `FORBIDDEN` |

### 3. 응답 직렬화

`HttpServletResponse`는 Spring의 `ResponseEntity` 흐름 밖에 있으므로, `ObjectMapper`로 직접 JSON을 작성했다.

```java
private void sendErrorResponse(HttpServletResponse response, HttpStatus status, ErrorCode errorCode)
        throws IOException {
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    objectMapper.writeValue(response.getWriter(), ApiResponse.failure(errorCode));
}
```

이 구조를 통해 프론트엔드는 메시지 문자열 해석 없이 `errorCode` 기준으로 토큰 재발급, 로그아웃, 안내 메시지 노출 같은 동작을 분기할 수 있게 됐다.


---
## ✅ 결과

인증·인가 실패도 다른 API 예외와 동일한 응답 형식으로 다룰 수 있게 됐다.
프론트엔드는 `errorCode`를 기준으로 토큰 재발급, 로그아웃, 안내 메시지 노출 같은 후속 처리를 분기할 수 있게 됐다.

또한 인증 실패 책임은 Security Layer에, 도메인 규칙 위반 책임은 애플리케이션 계층에 남기면서 계층 간 역할도 더 분명해졌다.

## 📝 정리

401/403 응답을 보안 계층에서 직접 공통 포맷으로 통일해, API 일관성, 프론트엔드 처리 편의성, 계층 책임 분리를 함께 확보했다.
