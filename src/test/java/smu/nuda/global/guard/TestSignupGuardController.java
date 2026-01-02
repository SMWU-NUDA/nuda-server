package smu.nuda.global.guard;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.nuda.domain.member.entity.enums.SignupStepType;
import smu.nuda.global.guard.annotation.SignupStep;

@RestController
class TestSignupGuardController {
    @SignupStep(SignupStepType.DELIVERY)
    @GetMapping("/test/auth/delivery")
    public void deliveryStepApi() {
        // AOP 트리거용
    }
}
