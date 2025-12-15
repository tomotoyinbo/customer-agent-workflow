package io.bly.customer.agent.domain.order;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * POC stub for order lookups.
 * In a real system this would call an external OrderService via REST/gRPC
 * or query a database.
 */
@Service
public class OrderService {

    private final Map<String, OrderData> orders = new HashMap<>();

    @PostConstruct
    void initFakeData() {
        // Seed a couple of fake orders for demo purposes
        orders.put("ORD-123456", OrderData.builder()
                .orderId("ORD-123456")
                .productSku("SKU-001")
                .productName("Wireless Headphones")
                .quantity(1)
                .shippingAddressLine1("123 Main St")
                .shippingAddressLine2("Apt 4B")
                .shippingCity("Seattle")
                .shippingState("WA")
                .shippingPostalCode("98101")
                .shippingCountry("US")
                .shippingMethod("GROUND")
                .trackingNumber("TRACK-123456")
                .customerName("Jane Doe")
                .customerEmail("jane.doe@example.com")
                .customerPhone("+1-555-0100")
                .build()
        );

        orders.put("ORD-987654", OrderData.builder()
                .orderId("ORD-987654")
                .productSku("SKU-002")
                .productName("Gaming Keyboard")
                .quantity(1)
                .shippingAddressLine1("456 Oak Ave")
                .shippingAddressLine2(null)
                .shippingCity("Portland")
                .shippingState("OR")
                .shippingPostalCode("97201")
                .shippingCountry("US")
                .shippingMethod("EXPRESS")
                .trackingNumber("TRACK-987654")
                .customerName("John Smith")
                .customerEmail("john.smith@example.com")
                .customerPhone("+1-555-0200")
                .build()
        );
    }

    public Optional<OrderData> findByOrderId(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }
}
