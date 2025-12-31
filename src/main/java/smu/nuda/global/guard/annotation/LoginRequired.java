package smu.nuda.global.guard.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginRequired {
    // 해당 API는 로그인이 반드시 필요함을 명시
}
