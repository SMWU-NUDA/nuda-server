package smu.nuda.domain.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.payment.dto.PaymentCompleteRequest;
import smu.nuda.domain.payment.dto.PaymentCompleteResponse;
import smu.nuda.domain.payment.dto.PaymentRequestResponse;
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
    public ApiResponse<PaymentRequestResponse> requestPayment(@PathVariable Long orderId) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(paymentService.requestPayment(member, orderId));
    }

    @PostMapping("/complete")
    @Operation(
            summary = "결제(Mock) 완료 콜백",
            description = "실제 PG사 승인 결과를 받아 주문 및 결제 상태를 업데이트합니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<PaymentCompleteResponse> completePayment( @Valid @RequestBody PaymentCompleteRequest request) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(paymentService.completePayment(member, request));
    }

    @PostMapping("/{paymentId}/complete-test")
    @Operation(
            summary = "결제(Mock) 테스트용 완료",
            description = "외부 PG사의 paymentKey 없이 서버 기준으로 즉시 결제 승인을 시도하는 테스트용 API입니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<PaymentCompleteResponse> completeTestPayment(@PathVariable Long paymentId) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(paymentService.completeTestPayment(member, paymentId));
    }

}
