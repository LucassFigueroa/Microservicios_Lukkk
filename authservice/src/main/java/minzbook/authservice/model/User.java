package minzbook.authservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String nombre;

    private String apellido;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role rol;

    private Boolean activo = true;

    private LocalDateTime fechaRegistro = LocalDateTime.now();
}
