package smu.nuda.domain.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.nuda.domain.common.dto.CursorPageResponse;
import smu.nuda.domain.order.dto.MyOrderResponse;
import smu.nuda.domain.order.dto.OrderCreateRequest;
import smu.nuda.domain.order.dto.OrderCreateResponse;
import smu.nuda.domain.order.dto.OrderItemRequest;
import smu.nuda.domain.order.service.OrderService;
import smu.nuda.global.guard.annotation.LoginRequired;
import smu.nuda.global.guard.guard.AuthenticationGuard;
import smu.nuda.global.response.ApiResponse;
import smu.nuda.global.swagger.annotation.AuthUnauthorizedErrorDocs;
import smu.nuda.global.swagger.annotation.CommonServerErrorDocs;
import smu.nuda.global.swagger.annotation.ValidationBadRequestDocs;
import smu.nuda.global.swagger.schema.ErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
@Tag(name = "[ORDER] 주문 API", description = "상품 주문 관련 API")
@CommonServerErrorDocs
public class OrderController {

    private final OrderService orderService;
    private final AuthenticationGuard authenticationGuard;

    @PostMapping
    @Operation(
            summary = "주문 생성",
            description = "장바구니에서 선택된 상품을 주문한다."
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
                                            name = "invalidProduct",
                                            summary = "주문 상품 없음",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "ORDER_INVALID_PRODUCT",
                                                      "message": "장바구니에 담긴 일부 상품을 찾을 수 없습니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalidCartItem",
                                            summary = "유효하지 않은 장바구니 아이템",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CART_INVALID_CART_ITEM",
                                                      "message": "존재하지 않거나 유효하지 않은 장바구니 아이템 ID입니다.",
                                                      "data": null
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "quantityMismatch",
                                            summary = "주문 수량 불일치",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "code": "CART_ORDER_QUANTITY_MISMATCH",
                                                      "message": "선택하신 상품 수량과 주문하려는 수량이 일치하지 않습니다.",
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
    public ApiResponse<OrderCreateResponse> createOrder(@Valid @RequestBody OrderCreateRequest orderCreateRequest) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(orderService.createOrder(memberId, orderCreateRequest));
    }

    @PostMapping("/direct")
    @Operation(
            summary = "바로 구매 주문 생성",
            description = "상품 상세페이지에서 바로구매 시 사용. 장바구니 담긴 상품과 무관하게 요청 수량으로 1회 요청에 1개 상품을 주문 생성한다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "도메인 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "directInvalidProduct",
                                    summary = "바로 구매 상품 없음",
                                    value = """
                                            {
                                              "success": false,
                                              "code": "ORDER_DIRECT_INVALID_PRODUCT",
                                              "message": "바로 결제할 상품을 찾을 수 없습니다.",
                                              "data": null
                                            }
                                            """
                            )
                    )
            )
    })
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    @ValidationBadRequestDocs
    public ApiResponse<OrderCreateResponse> directOrder(@Valid @RequestBody OrderItemRequest request) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(orderService.directOrder(memberId, request));
    }

    @GetMapping
    @Operation(
            summary = "나의 주문 목록 조회",
            description = "전체 주문 이력을 조회합니다. 최신 주문순으로 정렬되어 반환됩니다."
    )
    @SecurityRequirement(name = "JWT")
    @LoginRequired
    @AuthUnauthorizedErrorDocs
    @ValidationBadRequestDocs
    public ApiResponse<CursorPageResponse<MyOrderResponse>> getMyOrders(
            @Parameter(description = "이전 페이지의 마지막 id (첫 요청 시 null)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "한 페이지당 조회 개수 (기본값 20)") @RequestParam(defaultValue = "20") int size) {
        Long memberId = authenticationGuard.currentMemberId();
        return ApiResponse.success(orderService.getMyOrders(memberId, cursor, size));
    }

}
