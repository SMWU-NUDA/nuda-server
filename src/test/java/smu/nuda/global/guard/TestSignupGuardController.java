package smu.nuda.global.guard;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.nuda.domain.signupdraft.entity.enums.SignupStep;
import smu.nuda.domain.signupdraft.guard.annotation.SignupDraftStep;

@RestController
class TestSignupGuardController {
    @SignupDraftStep(SignupStep.DELIVERY)
    @GetMapping("/test/auth/delivery")
    public void deliveryStepApi() {
        // AOP 트리거용
    }
}
