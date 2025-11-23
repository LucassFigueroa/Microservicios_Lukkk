package minzbook.reviewservice.service;

import minzbook.reviewservice.dto.CreateReviewRequest;
import minzbook.reviewservice.dto.UpdateReviewRequest;
import minzbook.reviewservice.dto.ReviewResponse;
import minzbook.reviewservice.model.Review;
import minzbook.reviewservice.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void create_debeGuardarYRetornarReviewResponse() {
        // Given
        CreateReviewRequest request = new CreateReviewRequest();
        request.setBookId(1L);
        request.setUserId(2L);
        request.setRating(5);
        request.setComment("Excelente libro");

        Review saved = new Review();
        saved.setId(10L);
        saved.setBookId(1L);
        saved.setUserId(2L);
        saved.setRating(5);
        saved.setComment("Excelente libro");
        saved.setActivo(true);
        saved.setFechaCreacion(LocalDateTime.now());
        saved.setFechaActualizacion(saved.getFechaCreacion());

        when(reviewRepository.save(any(Review.class))).thenReturn(saved);

        // When
        ReviewResponse response = reviewService.create(request);

        // Then
        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(1L, response.getBookId());
        assertEquals(2L, response.getUserId());
        assertEquals(5, response.getRating());
        assertEquals("Excelente libro", response.getComment());

        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void getByBook_debeRetornarListaDeReviewsActivasPorLibro() {
        // Given
        Long bookId = 123L;

        Review r1 = new Review();
        r1.setId(1L);
        r1.setBookId(bookId);
        r1.setUserId(200L);
        r1.setRating(4);

        Review r2 = new Review();
        r2.setId(2L);
        r2.setBookId(bookId);
        r2.setUserId(201L);
        r2.setRating(5);

        when(reviewRepository.findByBookIdAndActivoTrue(bookId))
                .thenReturn(Arrays.asList(r1, r2));

        // When
        List<ReviewResponse> result = reviewService.getByBook(bookId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(bookId, result.get(0).getBookId());
        assertEquals(bookId, result.get(1).getBookId());

        verify(reviewRepository).findByBookIdAndActivoTrue(bookId);
    }

    @Test
    void getByUser_debeRetornarListaDeReviewsActivasPorUsuario() {
        // Given
        Long userId = 777L;

        Review r1 = new Review();
        r1.setId(1L);
        r1.setBookId(10L);
        r1.setUserId(userId);
        r1.setRating(3);

        Review r2 = new Review();
        r2.setId(2L);
        r2.setBookId(11L);
        r2.setUserId(userId);
        r2.setRating(5);

        when(reviewRepository.findByUserIdAndActivoTrue(userId))
                .thenReturn(Arrays.asList(r1, r2));

        // When
        List<ReviewResponse> result = reviewService.getByUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(userId, result.get(1).getUserId());

        verify(reviewRepository).findByUserIdAndActivoTrue(userId);
    }

    @Test
    void update_debeActualizarRatingYComentario() {
        // Given
        Long id = 1L;

        Review existing = new Review();
        existing.setId(id);
        existing.setBookId(100L);
        existing.setUserId(200L);
        existing.setRating(2);
        existing.setComment("Antes");
        existing.setActivo(true);
        existing.setFechaCreacion(LocalDateTime.now().minusDays(1));

        UpdateReviewRequest request = new UpdateReviewRequest();
        request.setRating(5);
        request.setComment("Ahora está excelente");

        when(reviewRepository.findById(id)).thenReturn(Optional.of(existing));
        when(reviewRepository.save(any(Review.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReviewResponse response = reviewService.update(id, request);

        // Then
        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(5, response.getRating());
        assertEquals("Ahora está excelente", response.getComment());
        assertNotNull(response.getFechaActualizacion());

        verify(reviewRepository).findById(id);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void update_debeLanzarExcepcionSiNoExiste() {
        // Given
        Long id = 99L;
        when(reviewRepository.findById(id)).thenReturn(Optional.empty());

        // When + Then
        assertThrows(RuntimeException.class,
                () -> reviewService.update(id, new UpdateReviewRequest()));

        verify(reviewRepository).findById(id);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void delete_debeMarcarReviewComoInactiva() {
        // Given
        Long id = 1L;
        Review existing = new Review();
        existing.setId(id);
        existing.setActivo(true);

        when(reviewRepository.findById(id)).thenReturn(Optional.of(existing));
        when(reviewRepository.save(any(Review.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);

        // When
        reviewService.delete(id);

        // Then
        verify(reviewRepository).findById(id);
        verify(reviewRepository).save(captor.capture());

        Review saved = captor.getValue();
        assertFalse(saved.getActivo(), "La reseña debería quedar inactiva (activo=false)");
        assertNotNull(saved.getFechaActualizacion());
    }

    @Test
    void delete_debeLanzarExcepcionSiNoExiste() {
        // Given
        Long id = 99L;
        when(reviewRepository.findById(id)).thenReturn(Optional.empty());

        // When + Then
        assertThrows(RuntimeException.class,
                () -> reviewService.delete(id));

        verify(reviewRepository).findById(id);
        verify(reviewRepository, never()).save(any());
    }
}
