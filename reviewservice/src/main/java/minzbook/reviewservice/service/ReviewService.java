package minzbook.reviewservice.service;

import minzbook.reviewservice.dto.CreateReviewRequest;
import minzbook.reviewservice.dto.UpdateReviewRequest;
import minzbook.reviewservice.dto.ReviewResponse;
import minzbook.reviewservice.model.Review;
import minzbook.reviewservice.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public ReviewResponse create(CreateReviewRequest request) {
        Review review = new Review();
        review.setBookId(request.getBookId());
        review.setUserId(request.getUserId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setActivo(true);
        review.setFechaCreacion(LocalDateTime.now());
        review.setFechaActualizacion(LocalDateTime.now());

        Review saved = reviewRepository.save(review);
        return toResponse(saved);
    }

    public List<ReviewResponse> getByBook(Long bookId) {
        return reviewRepository.findByBookIdAndActivoTrue(bookId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ReviewResponse> getByUser(Long userId) {
        return reviewRepository.findByUserIdAndActivoTrue(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ReviewResponse update(Long id, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setFechaActualizacion(LocalDateTime.now());

        return toResponse(reviewRepository.save(review));
    }

    // Borrado lógico
    public void delete(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));
        review.setActivo(false);
        review.setFechaActualizacion(LocalDateTime.now());
        reviewRepository.save(review);
    }

    private ReviewResponse toResponse(Review review) {
        ReviewResponse dto = new ReviewResponse();
        dto.setId(review.getId());
        dto.setBookId(review.getBookId());
        dto.setUserId(review.getUserId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setActivo(review.getActivo());
        dto.setFechaCreacion(review.getFechaCreacion());
        dto.setFechaActualizacion(review.getFechaActualizacion());
        return dto;
    }
}
