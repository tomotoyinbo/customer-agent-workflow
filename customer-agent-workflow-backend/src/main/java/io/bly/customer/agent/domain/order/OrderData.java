package io.bly.customer.agent.domain.order;

import lombok.Builder;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple in-memory representation of order + shipping + customer contact details.
 * For the POC, this is not persisted; it's returned by OrderService (stub).
 */
@Value
@Builder
public class OrderData {

    String orderId;

    // Product details
    String productSku;
    String productName;
    int quantity;

    // Shipping / fulfillment details
    String shippingAddressLine1;
    String shippingAddressLine2;
    String shippingCity;
    String shippingState;
    String shippingPostalCode;
    String shippingCountry;
    String shippingMethod;      // e.g., "GROUND", "EXPRESS"
    String trackingNumber;

    // Customer contact info
    String customerName;
    String customerEmail;
    String customerPhone;

    public Map<String, Object> productDetailsAsMap() {

        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        map.put("productSku", productSku);
        map.put("productName", productName);
        map.put("quantity", quantity);

        return map;
    }

    public Map<String, Object> shippingDetailsAsMap() {

        Map<String, Object> map = new HashMap<>();
        map.put("addressLine1", shippingAddressLine1);
        map.put("addressLine2", shippingAddressLine2);
        map.put("city", shippingCity);
        map.put("state", shippingState);
        map.put("postalCode", shippingPostalCode);
        map.put("country", shippingCountry);
        map.put("shippingMethod", shippingMethod);
        map.put("trackingNumber", trackingNumber);

        return map;
    }

    public Map<String, Object> customerContactAsMap() {

        Map<String, Object> map = new HashMap<>();
        map.put("name", customerName);
        map.put("email", customerEmail);
        map.put("phone", customerPhone);

        return map;
    }
}
