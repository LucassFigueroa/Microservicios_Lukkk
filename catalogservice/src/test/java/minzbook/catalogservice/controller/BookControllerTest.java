package minzbook.catalogservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import minzbook.catalogservice.dto.BookRequest;
import minzbook.catalogservice.dto.BookResponse;
import minzbook.catalogservice.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void health_ok() throws Exception {
        mockMvc.perform(get("/api/catalog/books/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Catalog service OK"));
    }

    @Test
    void getAll_ok() throws Exception {
        BookResponse b1 = new BookResponse();
        b1.setId(1L);
        b1.setTitulo("Libro 1");

        BookResponse b2 = new BookResponse();
        b2.setId(2L);
        b2.setTitulo("Libro 2");

        when(bookService.getAll()).thenReturn(List.of(b1, b2));

        mockMvc.perform(get("/api/catalog/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Libro 1"))
                .andExpect(jsonPath("$[1].titulo").value("Libro 2"));
    }

    @Test
    void getById_ok() throws Exception {
        BookResponse res = new BookResponse();
        res.setId(10L);
        res.setTitulo("Libro 10");

        when(bookService.getById(10L)).thenReturn(res);

        mockMvc.perform(get("/api/catalog/books/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.titulo").value("Libro 10"));
    }

    @Test
    void create_ok() throws Exception {
        // request con portada base64
        BookRequest req = new BookRequest();
        req.setTitulo("Nuevo libro");
        req.setAutor("Autor X");
        req.setDescripcion("Desc");
        req.setPrecio(1000.0);
        req.setStock(5);
        req.setCategoria("Ficción");
        req.setPortadaBase64("dGVzdA=="); // "test" en base64
        req.setPortadaContentType("image/png");

        BookResponse res = new BookResponse();
        res.setId(1L);
        res.setTitulo("Nuevo libro");
        res.setAutor("Autor X");
        res.setPrecio(1000.0);
        res.setCategoria("Ficción");
        res.setPortadaBase64("dGVzdA==");
        res.setPortadaContentType("image/png");

        when(bookService.create(any(BookRequest.class))).thenReturn(res);

        mockMvc.perform(
                        post("/api/catalog/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(req))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Nuevo libro"))
                .andExpect(jsonPath("$.autor").value("Autor X"))
                .andExpect(jsonPath("$.portadaBase64").value("dGVzdA=="))
                .andExpect(jsonPath("$.portadaContentType").value("image/png"));
    }

    @Test
    void update_ok() throws Exception {
        BookRequest req = new BookRequest();
        req.setTitulo("Actualizado");
        req.setAutor("Autor Y");
        req.setDescripcion("Desc nueva");
        req.setPrecio(1500.0);
        req.setStock(3);
        req.setCategoria("Ficción");
        req.setPortadaBase64(null);
        req.setPortadaContentType(null);

        BookResponse res = new BookResponse();
        res.setId(5L);
        res.setTitulo("Actualizado");
        res.setPrecio(1500.0);

        when(bookService.update(eq(5L), any(BookRequest.class))).thenReturn(res);

        mockMvc.perform(
                        put("/api/catalog/books/5")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(req))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.titulo").value("Actualizado"))
                .andExpect(jsonPath("$.precio").value(1500.0));
    }

    @Test
    void delete_forbiddenWhenNotAdmin() throws Exception {
        mockMvc.perform(
                        delete("/api/catalog/books/7")
                                .header("X-ROLE", "USER")
                )
                .andExpect(status().isForbidden())
                .andExpect(content().string("Solo un ADMIN puede eliminar libros."));
    }

    @Test
    void delete_okWhenAdmin() throws Exception {
        doNothing().when(bookService).delete(7L);

        mockMvc.perform(
                        delete("/api/catalog/books/7")
                                .header("X-ROLE", "ADMIN")
                )
                .andExpect(status().isNoContent());

        verify(bookService).delete(7L);
    }
}
