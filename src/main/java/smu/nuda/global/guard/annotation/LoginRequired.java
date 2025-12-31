package smu.nuda.global.guard.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginRequired {
    // 해당 API는 Access Token 으로 인증된 사용자만 접근 가능
}
