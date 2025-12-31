package smu.nuda.global.guard.annotation;

import smu.nuda.domain.member.entity.enums.SignupStepType;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SignupStep {
    // 해당 API는 회원가입의 SignupStepType 단계 이상만 요청 가능함을 명시
    SignupStepType value();
}
