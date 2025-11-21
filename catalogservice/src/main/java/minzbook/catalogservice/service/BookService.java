package minzbook.catalogservice.service;

import minzbook.catalogservice.dto.BookRequest;
import minzbook.catalogservice.dto.BookResponse;
import minzbook.catalogservice.model.Book;
import minzbook.catalogservice.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookResponse create(BookRequest request) {
        Book book = new Book();
        book.setTitulo(request.getTitulo());
        book.setAutor(request.getAutor());
        book.setDescripcion(request.getDescripcion());
        book.setPrecio(request.getPrecio());
        book.setStock(request.getStock());
        book.setCategoria(request.getCategoria());
        book.setImagenUrl(request.getImagenUrl());
        book.setActivo(true);

        Book saved = bookRepository.save(book);
        return toResponse(saved);
    }

    public List<BookResponse> getAll() {
        return bookRepository.findByActivoTrue()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public BookResponse getById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        return toResponse(book);
    }
    public BookResponse update(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        book.setTitulo(request.getTitulo());
        book.setAutor(request.getAutor());
        book.setDescripcion(request.getDescripcion());
        book.setPrecio(request.getPrecio());
        book.setStock(request.getStock());
        book.setCategoria(request.getCategoria());
        book.setImagenUrl(request.getImagenUrl());

        Book updated = bookRepository.save(book);
        return toResponse(updated);
    }

    public void delete(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        book.setActivo(false);
        bookRepository.save(book);
    }
    private BookResponse toResponse(Book book) {
        BookResponse dto = new BookResponse();
        dto.setId(book.getId());
        dto.setTitulo(book.getTitulo());
        dto.setAutor(book.getAutor());
        dto.setDescripcion(book.getDescripcion());
        dto.setPrecio(book.getPrecio());
        dto.setStock(book.getStock());
        dto.setCategoria(book.getCategoria());
        dto.setImagenUrl(book.getImagenUrl());
        dto.setActivo(book.getActivo());
        return dto;
    }
}
