package org.example.cart;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final ShoppingCartService service;

    public CartController(ShoppingCartService service) {
        this.service = service;
    }

    @PostMapping("/items")
    public ResponseEntity<CartDtos.CartResponse> add(
            @Valid @RequestBody CartDtos.AddItemRequest request) {

        service.addItem(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.viewCart());
    }

    @DeleteMapping("/item/{productId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long productId) {
        service.removeItem(productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    public CartDtos.CartResponse checkout() {
        return service.checkout();
    }
}
