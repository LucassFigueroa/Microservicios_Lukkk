package minzbook.orderservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders") //
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private LocalDateTime fechaCreacion = LocalDateTime.now();

    private Double total;

    @Enumerated(EnumType.STRING)
    private OrderStatus estado = OrderStatus.CREADA;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public OrderStatus getEstado() { return estado; }
    public void setEstado(OrderStatus estado) { this.estado = estado; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}
