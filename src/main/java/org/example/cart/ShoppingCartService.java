package org.example.cart;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ShoppingCartService {

    private final ProductRepository products;

    private final CartItemRepository items;

    private final Map<Category, DiscountStrategy> discounts;

    public ShoppingCartService(ProductRepository products, CartItemRepository items, List<DiscountStrategy> strategies) {
        this.products = products;
        this.items = items;
        discounts = new EnumMap<>(Category.class);

        for (DiscountStrategy strategy : strategies) {
            if (discounts.putIfAbsent(strategy.category(), strategy) != null) {
                throw new IllegalStateException("DuplicateDiscont Strategy");
            }
        }
    }

    @Transactional
    public Product createProduct(CartDtos.ProductRequest request) {
        Product product = new Product(
                request.name().trim(),
                request.category(),
                request.price().setScale(2, RoundingMode.HALF_UP)
        );

        return products.save(product);
    }

    public List<Product> getAllProducts() {
        return products.findAll();
    }

    @Transactional
    public synchronized void addItem(CartDtos.AddItemRequest request) {
        if (request.productId() == null || request.quantity() <= 0) {
            throw new NoSuchElementException("Item not found");
        }

        Product product = products.findById(request.productId())
                .orElseThrow(() -> new NoSuchElementException("Product not found"));

        Optional<CartItem> existing = items.findById(product.getId());

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(Math.addExact(item.getQuantity(), request.quantity()));
            items.save(item);
        } else {
            items.save(new CartItem(product, request.quantity()));
        }
    }

    @Transactional(readOnly = true)
    public synchronized CartDtos.CartResponse viewCart() {

        List<CartDtos.CartLine> lines = new ArrayList<>();
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (CartItem item : items.findAll()) {
            Product product = item.getProduct();

            BigDecimal amount = product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            DiscountStrategy strategy = discounts.get(product.getCategory());

            if (strategy == null) {
                throw new IllegalStateException("Discount strategy missing");
            }

            BigDecimal discount = strategy.calculate(amount).setScale(2, RoundingMode.HALF_UP);

            lines.add(new CartDtos.CartLine(product.getId(),
                    product.getName(),
                    item.getQuantity(),
                    amount,
                    discount,
                    amount.subtract(discount)));
            subTotal = subTotal.add(amount);
            totalDiscount = totalDiscount.add(discount);

        }

        return new CartDtos.CartResponse(
                List.copyOf(lines),
                subTotal,
                totalDiscount,
                subTotal.subtract(totalDiscount));

    }

    @Transactional
    public synchronized void removeItem(Long id) {
        CartItem item = items.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Cart item not found"));

        items.delete(item);
    }

    @Transactional
    public synchronized CartDtos.CartResponse checkout() {
        CartDtos.CartResponse bill = viewCart();

        if (bill.items().isEmpty()) {
            throw new IllegalStateException("Cart not empty");
        }

        items.deleteAll();
        return bill;
    }

}
