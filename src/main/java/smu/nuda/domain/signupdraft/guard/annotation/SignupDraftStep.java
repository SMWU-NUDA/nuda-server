package smu.nuda.domain.signupdraft.guard.annotation;

import smu.nuda.domain.signupdraft.entity.enums.SignupStep;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SignupDraftStep {
    SignupStep value();
}
