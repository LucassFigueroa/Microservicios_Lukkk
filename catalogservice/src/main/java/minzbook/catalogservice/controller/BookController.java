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

    // POST JSON normal (ya lo tenías)
    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(request));
    }

    // 🔹 NUEVO: POST con imagen (multipart/form-data)
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

        // Armamos el mismo BookRequest que usas en el POST normal
        BookRequest request = new BookRequest();
        request.setTitulo(titulo);
        request.setAutor(autor);
        request.setCategoria(categoria);
        request.setDescripcion(descripcion);
        request.setPrecio(precio);
        request.setStock(stock);
        request.setImagenUrl(imageUrl); // puede ser null si no se mandó imagen

        BookResponse response = bookService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAll() {
        return ResponseEntity.ok(bookService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request
    ) {
        return ResponseEntity.ok(bookService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
