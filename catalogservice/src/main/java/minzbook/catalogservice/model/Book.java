package minzbook.catalogservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String autor;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private String categoria;

    private Boolean activo = true;

    // ========== PORTADA EN BLOB ==========
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] portada;

    private String portadaContentType; // ej: "image/jpeg"
}
