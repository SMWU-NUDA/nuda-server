package smu.nuda.domain.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.payment.dto.PaymentReqResponse;
import smu.nuda.domain.payment.service.PaymentService;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Tag(name = "[PAYMENT] 결제 API", description = "결제 관련 API")
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping("/orders/{orderId}")
    @Operation(
            summary = "결제(Mock) 요청",
            description = "주문에 대한 결제 승인을 요청합니다." +
                    "실제 PG사 연동 전 단계로, 결제 수단 및 금액 정보를 검증하고 **결제 대기 상태(READY)**의 결제 데이터를 생성합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<PaymentReqResponse> requestPayment(@PathVariable Long orderId) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(paymentService.requestPayment(orderId));
    }
}
