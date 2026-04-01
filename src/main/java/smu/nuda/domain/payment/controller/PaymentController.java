package smu.nuda.domain.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import smu.nuda.global.swagger.annotation.AuthUnauthorizedErrorDocs;
import smu.nuda.global.swagger.annotation.CommonServerErrorDocs;
import smu.nuda.global.swagger.annotation.ValidationBadRequestDocs;
import smu.nuda.global.swagger.schema.ErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
@Tag(name = "[PAYMENT] 결제 API", description = "결제 관련 API")
@CommonServerErrorDocs
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping("/orders/{orderId}")
    @Operation(
            summary = "결제(Mock) 요청",
            description = "주문에 대한 결제 승인을 요청합니다." +
                    "실제 PG사 연동 전 단계로, 결제 수단 및 금액 정보를 검증하고 **결제 대기 상태(READY)**의 결제 데이터를 생성합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "orderNotFound",
                                            summary = "주문 없음",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "PAYMENT_ORDER_NOT_FOUND",
                                                      "message": "존재하지 않는 주문입니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "notOrderOwner",
                                            summary = "주문자 본인 아님",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "PAYMENT_NOT_ORDER_OWNER",
                                                      "message": "주문자 본인만 결제를 진행할 수 있습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalidPaymentStatus",
                                            summary = "결제 가능한 주문 상태 아님",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "PAYMENT_INVALID_PAYMENT_STATUS",
                                                      "message": "해당 주문은 결제 가능한 상태가 아닙니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "alreadyRequested",
                                            summary = "이미 결제 요청됨",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "PAYMENT_ALREADY_REQUESTED",
                                                      "message": "이미 결제가 진행 중이거나 완료된 주문입니다.",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<PaymentRequestResponse> requestPayment(@PathVariable Long orderId) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(paymentService.requestPayment(memberId, orderId));
    }

    @PostMapping("/complete")
    @Operation(
            summary = "결제(Mock) 완료 콜백",
            description = "실제 PG사 승인 결과를 받아 주문 및 결제 상태를 업데이트합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "paymentNotFound",
                                            summary = "결제 내역 없음",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "PAYMENT_NOT_FOUND",
                                                      "message": "결제 내역을 찾을 수 없습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "alreadyPaid",
                                            summary = "이미 결제 완료됨",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "PAYMENT_ALREADY_PAID",
                                                      "message": "이미 결제가 완료된 주문입니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "orderMismatch",
                                            summary = "주문 정보 불일치",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "PAYMENT_ORDER_MISMATCH",
                                                      "message": "결제 요청된 주문 정보가 일치하지 않습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalidAmount",
                                            summary = "결제 금액 불일치",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "PAYMENT_INVALID_AMOUNT",
                                                      "message": "결제 요청 금액과 실제 주문 금액이 일치하지 않습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    @ValidationBadRequestDocs
    public ApiResponse<PaymentCompleteResponse> completePayment( @Valid @RequestBody PaymentCompleteRequest request) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(paymentService.completePayment(member, request));
    }

    @PostMapping("/{paymentId}/complete-test")
    @Operation(
            summary = "결제(Mock) 테스트용 완료",
            description = "외부 PG사의 paymentKey 없이 서버 기준으로 즉시 결제 승인을 시도하는 테스트용 API입니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "paymentNotFound",
                                            summary = "결제 내역 없음",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "PAYMENT_NOT_FOUND",
                                                      "message": "결제 내역을 찾을 수 없습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "alreadyPaid",
                                            summary = "이미 결제 완료됨",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "PAYMENT_ALREADY_PAID",
                                                      "message": "이미 결제가 완료된 주문입니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalidPaymentStatus",
                                            summary = "결제 가능한 상태 아님",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "PAYMENT_INVALID_PAYMENT_STATUS",
                                                      "message": "해당 주문은 결제 가능한 상태가 아닙니다.",
                                                      "data": null
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    public ApiResponse<PaymentCompleteResponse> completeTestPayment(@PathVariable Long paymentId) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(paymentService.completeTestPayment(member, paymentId));
    }

}
