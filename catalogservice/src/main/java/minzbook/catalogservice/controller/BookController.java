package minzbook.catalogservice.controller;

import minzbook.catalogservice.dto.BookRequest;
import minzbook.catalogservice.dto.BookResponse;
import minzbook.catalogservice.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/catalog/books")
@CrossOrigin(origins = "*")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // =========================
    // HEALTH
    // =========================
    @GetMapping("/health")
    public String health() {
        return "Catalog service OK";
    }

    // =========================
    // LISTAR TODOS (solo activos)
    // =========================
    @GetMapping
    public List<BookResponse> getAll() {
        return bookService.getAll();
    }

    // =========================
    // OBTENER POR ID
    // =========================
    @GetMapping("/{id}")
    public BookResponse getById(@PathVariable Long id) {
        return bookService.getById(id);
    }

    // =========================
    // ⚠ NUEVO: OBTENER PORTADA (BLOB)
    // =========================
    @GetMapping("/{id}/cover")
    public ResponseEntity<byte[]> getCover(@PathVariable Long id) {
        byte[] data = bookService.getCoverBytes(id);
        if (data == null || data.length == 0) {
            return ResponseEntity.notFound().build();
        }

        String contentType = bookService.getCoverContentType(id);
        MediaType mediaType = (contentType != null && !contentType.isBlank())
                ? MediaType.parseMediaType(contentType)
                : MediaType.IMAGE_JPEG;

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(data);
    }

    // =========================
    // CREAR
    // =========================
    @PostMapping
    public ResponseEntity<BookResponse> create(@RequestBody BookRequest request) {
        BookResponse created = bookService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // =========================
    // ACTUALIZAR
    // =========================
    @PutMapping("/{id}")
    public BookResponse update(
            @PathVariable Long id,
            @RequestBody BookRequest request
    ) {
        return bookService.update(id, request);
    }

    // =========================
    // ELIMINAR (borrado lógico)
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
