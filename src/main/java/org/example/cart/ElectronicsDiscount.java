package org.example.cart;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ElectronicsDiscount implements DiscountStrategy{
    @Override
    public Category category() {
        return Category.ELECTRONICS;
    }

    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.10"));
    }
}
