package org.example.cart;

import java.math.BigDecimal;

public interface DiscountStrategy {

    Category category();

    BigDecimal calculate(BigDecimal amount);
}
