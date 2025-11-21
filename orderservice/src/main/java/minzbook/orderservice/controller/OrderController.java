package minzbook.orderservice.controller;

import jakarta.validation.Valid;
import minzbook.orderservice.dto.*;
import minzbook.orderservice.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/health")
    public String health() {
        return "Order service OK";
    }

    // ===== Carrito =====

    @PostMapping("/cart")
    public ResponseEntity<CartItemResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.addToCart(request));
    }

    @GetMapping("/cart/{userId}")
    public ResponseEntity<List<CartItemResponse>> getCart(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(orderService.getCartByUser(userId));
    }

    @DeleteMapping("/cart/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable Long cartItemId
    ) {
        orderService.removeFromCart(cartItemId);
        return ResponseEntity.noContent().build();
    }

    // ===== Checkout / Órdenes =====

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(
            @Valid @RequestBody CheckoutRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.checkout(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }
}
