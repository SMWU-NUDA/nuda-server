package smu.nuda.global.guard.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface SignupTokenRequired {
    // 해당 API는 Signup Token 으로 인증된 사용자만 접근 가능
}
