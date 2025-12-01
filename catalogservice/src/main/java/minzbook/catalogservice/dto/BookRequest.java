package minzbook.catalogservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookRequest {
    private String titulo;
    private String autor;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private String categoria;

    private String portadaBase64;       // image encoded
    private String portadaContentType;  // ej: image/png
}
