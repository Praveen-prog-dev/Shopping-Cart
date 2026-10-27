package org.example.cart;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ClothingDiscount implements DiscountStrategy{
    @Override
    public Category category() {
        return Category.CLOTHING;
    }

    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.20"));
    }
}
