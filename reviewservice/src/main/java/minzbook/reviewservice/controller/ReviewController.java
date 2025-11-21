package minzbook.reviewservice.controller;

import jakarta.validation.Valid;
import minzbook.reviewservice.dto.CreateReviewRequest;
import minzbook.reviewservice.dto.UpdateReviewRequest;
import minzbook.reviewservice.dto.ReviewResponse;
import minzbook.reviewservice.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.ok(reviewService.create(request));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewResponse>> getByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getByBook(bookId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getByUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewRequest request) {
        return ResponseEntity.ok(reviewService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public String health() {
        return "Review service OK";
    }
}
