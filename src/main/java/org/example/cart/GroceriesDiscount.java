package org.example.cart;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class GroceriesDiscount implements DiscountStrategy{
    @Override
    public Category category() {
        return Category.GROCERIES;
    }

    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return amount.multiply(new BigDecimal("0.30"));
    }
}
