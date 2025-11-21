package minzbook.orderservice.service;

import minzbook.orderservice.dto.*;
import minzbook.orderservice.model.CartItem;
import minzbook.orderservice.model.Order;
import minzbook.orderservice.model.OrderItem;
import minzbook.orderservice.model.OrderStatus;
import minzbook.orderservice.repository.CartItemRepository;
import minzbook.orderservice.repository.OrderItemRepository;
import minzbook.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(
            CartItemRepository cartItemRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository
    ) {
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // ===== Carrito =====

    public CartItemResponse addToCart(AddToCartRequest request) {

        CartItem item = new CartItem();
        item.setUserId(request.getUserId());
        item.setBookId(request.getBookId());
        item.setCantidad(request.getCantidad());
        item.setPrecioUnitario(request.getPrecioUnitario());
        item.setTitulo(request.getTitulo());

        CartItem saved = cartItemRepository.save(item);
        return toCartResponse(saved);
    }

    public List<CartItemResponse> getCartByUser(Long userId) {
        return cartItemRepository.findByUserId(userId)
                .stream()
                .map(this::toCartResponse)
                .collect(Collectors.toList());
    }

    public void removeFromCart(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new RuntimeException("Item de carrito no encontrado");
        }
        cartItemRepository.deleteById(cartItemId);
    }

    // ===== Checkout / Orden =====

    public OrderResponse checkout(CheckoutRequest request) {

        List<CartItem> cartItems = cartItemRepository.findByUserId(request.getUserId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        Order order = new Order();
        order.setUserId(request.getUserId());

        // Calcular total
        double total = cartItems.stream()
                .mapToDouble(ci -> ci.getPrecioUnitario() * ci.getCantidad())
                .sum();
        order.setTotal(total);
        order.setEstado(OrderStatus.CREADA);

        // Guardar orden primero
        Order savedOrder = orderRepository.save(order);

        // Crear items de orden
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setOrder(savedOrder);
            oi.setBookId(ci.getBookId());
            oi.setTitulo(ci.getTitulo());
            oi.setCantidad(ci.getCantidad());
            oi.setPrecioUnitario(ci.getPrecioUnitario());
            orderItems.add(oi);
        }

        orderItemRepository.saveAll(orderItems);
        savedOrder.setItems(orderItems);

        // Limpiar carrito
        cartItemRepository.deleteAll(cartItems);

        return toOrderResponse(savedOrder);
    }

    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        return toOrderResponse(order);
    }

    // ===== Mappers =====

    private CartItemResponse toCartResponse(CartItem item) {
        CartItemResponse dto = new CartItemResponse();
        dto.setId(item.getId());
        dto.setUserId(item.getUserId());
        dto.setBookId(item.getBookId());
        dto.setTitulo(item.getTitulo());
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());
        dto.setSubtotal(item.getPrecioUnitario() * item.getCantidad());
        return dto;
    }

    private OrderResponse toOrderResponse(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setFechaCreacion(order.getFechaCreacion());
        dto.setTotal(order.getTotal());
        dto.setEstado(order.getEstado().name());

        List<OrderItemResponse> itemsDto = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem oi : order.getItems()) {
                OrderItemResponse ir = new OrderItemResponse();
                ir.setId(oi.getId());
                ir.setBookId(oi.getBookId());
                ir.setTitulo(oi.getTitulo());
                ir.setCantidad(oi.getCantidad());
                ir.setPrecioUnitario(oi.getPrecioUnitario());
                itemsDto.add(ir);
            }
        }
        dto.setItems(itemsDto);

        return dto;
    }
}
