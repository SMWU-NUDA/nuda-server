package smu.nuda.global.guard.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SignupCompleted {
    // 해당 API는 회원가입이 완료된 사용자만 요청 가능함을 명시
}
