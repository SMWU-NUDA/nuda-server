package smu.nuda.domain.order.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import smu.nuda.domain.brand.entity.Brand;
import smu.nuda.domain.order.dto.OrderBrandGroup;
import smu.nuda.domain.order.dto.OrderProductItem;
import smu.nuda.domain.order.entity.Order;
import smu.nuda.domain.order.entity.OrderItem;
import smu.nuda.domain.product.entity.Product;
import smu.nuda.domain.product.repository.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final ProductRepository productRepository;

    public List<OrderBrandGroup> toBrandGroups(Order order) {

        List<Long> productIds = order.getOrderItems().stream()
                .map(OrderItem::getProductId)
                .distinct()
                .toList();

        Map<Long, Product> productMap = productRepository.findAllWithBrandByIdIn(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return toBrandGroups(order, productMap);
    }

    public List<OrderBrandGroup> toBrandGroups(Order order, Map<Long, Product> productMap) {
        Map<Brand, List<OrderItem>> grouped = order.getOrderItems()
                .stream().collect(Collectors.groupingBy(
                        item -> productMap.get(item.getProductId()).getBrand()
                ));

        return grouped.entrySet().stream()
                .map(entry -> {
                    Brand brand = entry.getKey();
                    List<OrderItem> items = entry.getValue();

                    List<OrderProductItem> productItems = items.stream()
                            .map(item -> {
                                Product product = productMap.get(item.getProductId());
                                return new OrderProductItem(
                                        product.getId(),
                                        product.getName(),
                                        item.getQuantity(),
                                        item.getUnitPrice(),
                                        item.getQuantity() * item.getUnitPrice()
                                );
                            })
                            .toList();

                    return new OrderBrandGroup(
                            brand.getId(),
                            brand.getName(),
                            productItems
                    );
                })
                .toList();
    }
}

