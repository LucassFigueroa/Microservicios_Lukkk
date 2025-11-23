package minzbook.catalogservice.service;

import minzbook.catalogservice.dto.BookRequest;
import minzbook.catalogservice.dto.BookResponse;
import minzbook.catalogservice.model.Book;
import minzbook.catalogservice.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    // =========================
    // CREATE
    // =========================
    @Test
    void create_guardaYRetornaBookResponse() {
        BookRequest request = new BookRequest();
        request.setTitulo("Libro Test");
        request.setAutor("Autor Test");
        request.setDescripcion("Descripción");
        request.setPrecio(9990.0);
        request.setStock(5);
        request.setCategoria("Ficción");

        Book saved = new Book();
        saved.setId(1L);
        saved.setTitulo(request.getTitulo());
        saved.setAutor(request.getAutor());
        saved.setDescripcion(request.getDescripcion());
        saved.setPrecio(request.getPrecio());
        saved.setStock(request.getStock());
        saved.setCategoria(request.getCategoria());
        saved.setActivo(true);

        when(bookRepository.save(any(Book.class))).thenReturn(saved);

        BookResponse response = bookService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Libro Test", response.getTitulo());
        assertEquals("Autor Test", response.getAutor());

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());

        Book toSave = captor.getValue();
        assertEquals("Libro Test", toSave.getTitulo());
        assertEquals("Autor Test", toSave.getAutor());
        assertTrue(toSave.getActivo());
    }

    // =========================
    // GET ALL
    // =========================
    @Test
    void getAll_retornaLista() {
        Book b1 = new Book();
        b1.setId(1L);
        b1.setTitulo("Libro 1");
        b1.setActivo(true);

        Book b2 = new Book();
        b2.setId(2L);
        b2.setTitulo("Libro 2");
        b2.setActivo(true);


        when(bookRepository.findByActivoTrue()).thenReturn(Arrays.asList(b1, b2));

        List<BookResponse> list = bookService.getAll();

        assertEquals(2, list.size());
        assertEquals("Libro 1", list.get(0).getTitulo());
        assertEquals("Libro 2", list.get(1).getTitulo());
    }

    // =========================
    // GET BY ID
    // =========================
    @Test
    void getById_ok() {
        Book book = new Book();
        book.setId(10L);
        book.setTitulo("Libro 10");

        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        BookResponse res = bookService.getById(10L);

        assertEquals(10L, res.getId());
        assertEquals("Libro 10", res.getTitulo());
    }

    @Test
    void getById_noExiste() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookService.getById(99L));
    }

    // =========================
    // UPDATE
    // =========================
    @Test
    void update_ok() {
        Book existing = new Book();
        existing.setId(5L);
        existing.setTitulo("Viejo");

        BookRequest req = new BookRequest();
        req.setTitulo("Nuevo");
        req.setPrecio(1500.0);
        req.setStock(3);
        req.setCategoria("Ficción");

        Book updated = new Book();
        updated.setId(5L);
        updated.setTitulo("Nuevo");

        when(bookRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(bookRepository.save(any(Book.class))).thenReturn(updated);

        BookResponse res = bookService.update(5L, req);

        assertEquals(5L, res.getId());
        assertEquals("Nuevo", res.getTitulo());
    }

    @Test
    void update_noExiste() {
        when(bookRepository.findById(5L)).thenReturn(Optional.empty());

        BookRequest req = new BookRequest();
        req.setTitulo("Nuevo");
        req.setPrecio(1500.0);
        req.setStock(3);
        req.setCategoria("Ficción");

        assertThrows(RuntimeException.class, () -> bookService.update(5L, req));
        verify(bookRepository, never()).save(any(Book.class));
    }

    // =========================
    // DELETE (borrado lógico: activo = false)
    // =========================
    @Test
    void delete_ok() {
        Book book = new Book();
        book.setId(7L);
        book.setActivo(true);

        when(bookRepository.findById(7L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        bookService.delete(7L);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());

        Book saved = captor.getValue();
        assertEquals(7L, saved.getId());
        assertFalse(saved.getActivo()); 
    }
}
