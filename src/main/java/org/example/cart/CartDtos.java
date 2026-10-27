package org.example.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class CartDtos {

    record ProductRequest(
            @NotBlank String name,
            @NotNull Category category,
            @NotNull BigDecimal price
    ) {
    }

    record AddItemRequest(
            @NotNull Long productId,
            @NotNull @Min(1) int quantity
    ) {
    }

    record CartLine(
            Long productId,
            String productName,
            int quantity,
            BigDecimal subTotal,
            BigDecimal discount,
            BigDecimal finalPrice
    ) {
    }

    record CartResponse(
            List<CartLine> items,
            BigDecimal subTotal,
            BigDecimal finalDiscount,
            BigDecimal finalPrice
    ) {
    }
}
