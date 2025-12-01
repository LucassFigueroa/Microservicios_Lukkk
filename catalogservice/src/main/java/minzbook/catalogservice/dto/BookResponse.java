package minzbook.catalogservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookResponse {
    private Long id;
    private String titulo;
    private String autor;
    private String descripcion;
    private Double precio;
    private Integer stock;
    private String categoria;
    private Boolean activo;

    private String portadaBase64;       // enviada al front
    private String portadaContentType;
}
