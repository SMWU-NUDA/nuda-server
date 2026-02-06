package smu.nuda.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.nuda.domain.member.entity.Member;
import smu.nuda.domain.order.dto.*;
import smu.nuda.domain.order.entity.Order;
import smu.nuda.domain.order.entity.OrderItem;
import smu.nuda.domain.order.error.OrderErrorCode;
import smu.nuda.domain.order.policy.OrderNumberPolicy;
import smu.nuda.domain.order.repository.OrderItemRepository;
import smu.nuda.domain.order.repository.OrderRepository;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.repository.ProductRepository;
import smu.nuda.global.error.DomainException;

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
    private final OrderItemRepository orderItemRepository;
    private final OrderNumberPolicy orderNumberPolicy;

    @Transactional
    public OrderCreateResponse createOrder(Member member, OrderCreateRequest request) {

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
        Long orderNum = orderNumberPolicy.generate();
        Order order = Order.create(member.getId(), orderNum, totalAmount);
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

        // Brand 별 상품 그룹핑
        Map<Long, List<OrderItem>> brandGrouped = orderItems.stream()
                .collect(Collectors.groupingBy(
                item -> productMap.get(item.getProductId()).getBrand().getId()
        ));

        List<OrderBrandGroup> brandGroups = brandGrouped.entrySet().stream()
                .map(entry -> {
                    Long brandId = entry.getKey();
                    List<OrderItem> items = entry.getValue();

                    Product firstProduct = productMap.get(items.get(0).getProductId());

                    return new OrderBrandGroup(
                            brandId,
                            firstProduct.getBrand().getName(),
                            items.stream()
                                    .map(item -> {
                                        Product product = productMap.get(item.getProductId());
                                        return new OrderProductItem(
                                                item.getProductId(),
                                                product.getName(),
                                                item.getQuantity(),
                                                item.getUnitPrice(),
                                                item.getPrice()
                                        );
                                    })
                                    .toList()
                    );
                })
                .toList();

        return new OrderCreateResponse(
                order.getId(),
                order.getOrderNum(),
                order.getStatus().name(),
                order.getTotalAmount(),
                brandGroups
        );

    }

}
