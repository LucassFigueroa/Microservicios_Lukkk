package minzbook.reviewservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import minzbook.reviewservice.dto.CreateReviewRequest;
import minzbook.reviewservice.dto.UpdateReviewRequest;
import minzbook.reviewservice.dto.ReviewResponse;
import minzbook.reviewservice.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false) // desactiva filtros de seguridad
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    private ReviewResponse buildSampleResponse(Long id, Long bookId, Long userId) {
        ReviewResponse resp = new ReviewResponse();
        resp.setId(id);
        resp.setBookId(bookId);
        resp.setUserId(userId);
        resp.setRating(5);
        resp.setComment("Comentario de prueba");
        resp.setActivo(true);
        resp.setFechaCreacion(LocalDateTime.now().minusDays(1));
        resp.setFechaActualizacion(LocalDateTime.now());
        return resp;
    }

    @Test
    void health_debeResponderOk() throws Exception {
        mockMvc.perform(get("/reviews/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Review service OK"));
    }

    @Test
    void create_debeRetornarReviewCreada() throws Exception {
        // Given
        CreateReviewRequest request = new CreateReviewRequest();
        request.setBookId(1L);
        request.setUserId(2L);
        request.setRating(4);
        request.setComment("Muy bueno");

        ReviewResponse response = buildSampleResponse(10L, 1L, 2L);

        when(reviewService.create(any(CreateReviewRequest.class))).thenReturn(response);

        // When + Then
        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.bookId").value(1L))
                .andExpect(jsonPath("$.userId").value(2L));

        verify(reviewService).create(any(CreateReviewRequest.class));
    }

    @Test
    void getByBook_debeRetornarListaDeReviewsPorLibro() throws Exception {
        // Given
        Long bookId = 123L;
        ReviewResponse r1 = buildSampleResponse(1L, bookId, 200L);
        ReviewResponse r2 = buildSampleResponse(2L, bookId, 201L);
        List<ReviewResponse> list = Arrays.asList(r1, r2);

        when(reviewService.getByBook(bookId)).thenReturn(list);

        // When + Then
        mockMvc.perform(get("/reviews/book/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookId").value(bookId))
                .andExpect(jsonPath("$[1].bookId").value(bookId));

        verify(reviewService).getByBook(bookId);
    }

    @Test
    void getByUser_debeRetornarListaDeReviewsPorUsuario() throws Exception {
        // Given
        Long userId = 777L;
        ReviewResponse r1 = buildSampleResponse(1L, 100L, userId);
        ReviewResponse r2 = buildSampleResponse(2L, 101L, userId);
        List<ReviewResponse> list = Arrays.asList(r1, r2);

        when(reviewService.getByUser(userId)).thenReturn(list);

        // When + Then
        mockMvc.perform(get("/reviews/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[1].userId").value(userId));

        verify(reviewService).getByUser(userId);
    }

    @Test
    void update_debeRetornarReviewActualizada() throws Exception {
        // Given
        Long id = 7L;

        UpdateReviewRequest request = new UpdateReviewRequest();
        request.setRating(3);
        request.setComment("Actualizado");

        ReviewResponse updated = buildSampleResponse(id, 100L, 200L);
        updated.setRating(3);
        updated.setComment("Actualizado");

        when(reviewService.update(eq(id), any(UpdateReviewRequest.class)))
                .thenReturn(updated);

        // When + Then
        mockMvc.perform(put("/reviews/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.rating").value(3))
                .andExpect(jsonPath("$.comment").value("Actualizado"));

        verify(reviewService).update(eq(id), any(UpdateReviewRequest.class));
    }

    @Test
    void delete_debeRetornarNoContent() throws Exception {
        // Given
        Long id = 9L;

        // When + Then
        mockMvc.perform(delete("/reviews/{id}", id))
                .andExpect(status().isNoContent());

        verify(reviewService).delete(id);
    }
}
