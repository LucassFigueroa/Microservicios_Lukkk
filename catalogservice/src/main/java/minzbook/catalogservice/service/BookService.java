package minzbook.catalogservice.service;

import minzbook.catalogservice.dto.BookRequest;
import minzbook.catalogservice.dto.BookResponse;
import minzbook.catalogservice.model.Book;
import minzbook.catalogservice.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository repo) {
        this.bookRepository = repo;
    }

    // ========== CREATE ==========
    public BookResponse create(BookRequest req) {
        Book book = new Book();

        book.setTitulo(req.getTitulo());
        book.setAutor(req.getAutor());
        book.setDescripcion(req.getDescripcion());
        book.setPrecio(req.getPrecio());
        book.setStock(req.getStock());
        book.setCategoria(req.getCategoria());
        book.setActivo(true);

        // Manejo del BLOB
        if (req.getPortadaBase64() != null) {
            book.setPortada(Base64.getDecoder().decode(req.getPortadaBase64()));
            book.setPortadaContentType(req.getPortadaContentType());
        }

        Book saved = bookRepository.save(book);
        return toResponse(saved);
    }

    // ========== GET ALL ==========
    public List<BookResponse> getAll() {
        return bookRepository.findByActivoTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ========== GET BY ID ==========
    public BookResponse getById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        return toResponse(book);
    }

    // ========== UPDATE ==========
    public BookResponse update(Long id, BookRequest req) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        book.setTitulo(req.getTitulo());
        book.setAutor(req.getAutor());
        book.setDescripcion(req.getDescripcion());
        book.setPrecio(req.getPrecio());
        book.setStock(req.getStock());
        book.setCategoria(req.getCategoria());

        if (req.getPortadaBase64() != null) {
            book.setPortada(Base64.getDecoder().decode(req.getPortadaBase64()));
            book.setPortadaContentType(req.getPortadaContentType());
        }

        return toResponse(bookRepository.save(book));
    }

    // ========== DELETE (lógico) ==========
    public void delete(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        book.setActivo(false);
        bookRepository.save(book);
    }

    // ========== MAPEADOR ==========
    private BookResponse toResponse(Book book) {
        BookResponse res = new BookResponse();

        res.setId(book.getId());
        res.setTitulo(book.getTitulo());
        res.setAutor(book.getAutor());
        res.setDescripcion(book.getDescripcion());
        res.setPrecio(book.getPrecio());
        res.setStock(book.getStock());
        res.setCategoria(book.getCategoria());
        res.setActivo(book.getActivo());

        if (book.getPortada() != null) {
            res.setPortadaBase64(Base64.getEncoder().encodeToString(book.getPortada()));
            res.setPortadaContentType(book.getPortadaContentType());
        }

        return res;
    }
}
