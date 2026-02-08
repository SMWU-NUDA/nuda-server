package smu.nuda.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.common.dto.CursorPageResponse;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.order.dto.MyOrderResponse;
import smu.nuda.domain.order.dto.OrderCreateRequest;
import smu.nuda.domain.order.dto.OrderCreateResponse;
import smu.nuda.domain.order.service.OrderService;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "[ORDER] 주문 API", description = "상품 주문 관련 API")
public class OrderController {

    private final OrderService orderService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping
    @Operation(
            summary = "주문 생성",
            description = "장바구니에서 선택된 상품을 주문한다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<OrderCreateResponse> createOrder(@Valid @RequestBody OrderCreateRequest orderCreateRequest) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(orderService.createOrder(member, orderCreateRequest));
    }

    @GetMapping
    @Operation(
            summary = "주문 내역 조회",
            description = "전체 주문 이력을 조회합니다. 최신 주문순으로 정렬되어 반환됩니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    public ApiResponse<CursorPageResponse<MyOrderResponse>> getMyOrders(
            @Parameter(description = "이전 페이지의 마지막 id (첫 요청 시 null)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "한 페이지당 조회 개수 (기본값 20)") @RequestParam(defaultValue = "20") int size) {
        Member member = authenticationGuard.currentMember();
        return ApiResponse.success(orderService.getMyOrders(member, cursor, size));
    }

}
