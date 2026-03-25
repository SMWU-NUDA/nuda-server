package smu.nuda.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.cart.service.CartService;
import smu.nuda.domain.common.dto.CursorPageResponse;
import smu.nuda.domain.order.dto.*;
import smu.nuda.domain.order.entity.Order;
import smu.nuda.domain.order.entity.OrderItem;
import smu.nuda.domain.order.entity.enums.OrderType;
import smu.nuda.domain.order.error.OrderErrorCode;
import smu.nuda.domain.order.mapper.OrderMapper;
import smu.nuda.domain.order.policy.OrderNumPolicy;
import smu.nuda.domain.order.repository.OrderItemRepository;
import smu.nuda.domain.order.repository.OrderQueryRepository;
import smu.nuda.domain.order.repository.OrderRepository;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.global.error.DomainException;
import smu.nuda.global.util.DateFormatUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderNumPolicy orderNumPolicy;
    private final CartService cartService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderCreateResponse createOrder(Long memberId, OrderCreateRequest request) {

        // Cart 기준 주문 가능 여부 검증
        cartService.validateOrderableItems(memberId, request.getItems());

        // 주문할 상품 ID 추출
        List<Long> productIds = request.getItems().stream()
                .map(OrderItemRequest::getProductId)
                .distinct()
                .toList();

        // 주문할 상품 일괄 조회 및 존재 검증
        List<Product> products = productRepository.findAllByIdIn(productIds);
        if (products.size() != productIds.size()) throw new DomainException(OrderErrorCode.INVALID_PRODUCT);

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        // 주문 금액 계산
        int totalAmount = 0;
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productMap.get(itemRequest.getProductId());
            totalAmount += product.getCostPrice() * itemRequest.getQuantity();
        }

        // Order 생성
        Long orderNum = orderNumPolicy.generate();
        Order order = Order.create(memberId, orderNum, totalAmount, OrderType.CART);
        orderRepository.save(order);

        // OrderItem 생성
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productMap.get(itemRequest.getProductId());

            OrderItem orderItem = OrderItem.create(
                    order,
                    product.getId(),
                    product.getCostPrice(),
                    itemRequest.getQuantity()
            );
            orderItems.add(orderItem);
        }
        orderItemRepository.saveAll(orderItems);

        List<OrderBrandGroup> brandGroups = orderMapper.toBrandGroups(order);

        return new OrderCreateResponse(
                order.getId(),
                order.getOrderNum(),
                order.getStatus(),
                order.getTotalAmount(),
                brandGroups
        );

    }

    @Transactional
    public OrderCreateResponse directOrder(Long memberId, OrderItemRequest item) {
        // 상품 존재 검증 (장바구니 검증 없음)
        Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new DomainException(OrderErrorCode.DIRECT_INVALID_PRODUCT));

        int totalAmount = product.getCostPrice() * item.getQuantity();

        Long orderNum = orderNumPolicy.generate();
        Order order = Order.create(memberId, orderNum, totalAmount, OrderType.DIRECT);
        orderRepository.save(order);

        OrderItem orderItem = OrderItem.create(
                order,
                product.getId(),
                product.getCostPrice(),
                item.getQuantity()
        );
        orderItemRepository.save(orderItem);

        List<OrderBrandGroup> brandGroups = orderMapper.toBrandGroups(order);
        return new OrderCreateResponse(
                order.getId(),
                order.getOrderNum(),
                order.getStatus(),
                order.getTotalAmount(),
                brandGroups
        );
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<MyOrderResponse> getMyOrders(Long memberId, Long cursor, int size) {

        // member의 Order 조회
        List<Order> orders = orderQueryRepository.findMyOrders(memberId, cursor, size);

        // 모든 OrderItem 수집
        List<OrderItem> allItems = orders.stream()
                .flatMap(o -> o.getOrderItems().stream())
                .toList();

        // Product, Brand 일괄 조회
        List<Long> productIds = allItems.stream()
                .map(OrderItem::getProductId)
                .distinct()
                .toList();

        Map<Long, Product> productMap = productRepository.findAllWithBrandByIdIn(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<MyOrderResponse> responses = orders.stream()
                .map(order -> new MyOrderResponse(
                        order.getId(),
                        DateFormatUtil.formatDate(order.getCreatedAt()),
                        order.getOrderNum(),
                        order.getTotalAmount(),
                        orderMapper.toBrandGroups(order, productMap)
                ))
                .toList();

        return CursorPageResponse.of(
                responses,
                size,
                MyOrderResponse::getOrderId
        );
    }
}