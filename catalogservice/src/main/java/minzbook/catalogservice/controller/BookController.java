package minzbook.catalogservice.controller;

import jakarta.validation.Valid;
import minzbook.catalogservice.dto.BookRequest;
import minzbook.catalogservice.dto.BookResponse;
import minzbook.catalogservice.service.BookService;
import minzbook.catalogservice.service.ImageStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/catalog/books")
@CrossOrigin(origins = "*")
public class BookController {

    private final BookService bookService;
    private final ImageStorageService imageStorageService;

    public BookController(BookService bookService,
                          ImageStorageService imageStorageService) {
        this.bookService = bookService;
        this.imageStorageService = imageStorageService;
    }

    @GetMapping("/health")
    public String health() {
        return "Catalog service OK";
    }

    // =========================
    // CREAR LIBRO (JSON)
    // =========================
    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(request));
    }

    // =========================
    // CREAR LIBRO CON IMAGEN
    // =========================
    @PostMapping(
            path = "/with-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<BookResponse> createWithImage(
            @RequestPart("titulo") String titulo,
            @RequestPart("autor") String autor,
            @RequestPart("categoria") String categoria,
            @RequestPart("descripcion") String descripcion,
            @RequestPart("precio") Double precio,
            @RequestPart("stock") Integer stock,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = imageStorageService.store(image);
        }

        BookRequest request = new BookRequest();
        request.setTitulo(titulo);
        request.setAutor(autor);
        request.setCategoria(categoria);
        request.setDescripcion(descripcion);
        request.setPrecio(precio);
        request.setStock(stock);
        request.setImagenUrl(imageUrl);

        BookResponse response = bookService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =========================
    // LISTAR TODOS
    // =========================
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAll() {
        return ResponseEntity.ok(bookService.getAll());
    }

    // =========================
    // OBTENER POR ID
    // =========================
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    // =========================
    // ACTUALIZAR
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request
    ) {
        return ResponseEntity.ok(bookService.update(id, request));
    }

    // =========================
    // ELIMINAR (SOLO ADMIN)
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @RequestHeader("X-ROLE") String role
    ) {
        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Solo un ADMIN puede eliminar libros.");
        }

        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
