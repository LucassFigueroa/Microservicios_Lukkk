package minzbook.catalogservice.service;

import minzbook.catalogservice.dto.ReviewDto;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ReviewClient {

    private final WebClient reviewClient;

    public ReviewClient(WebClient reviewClient) {
        this.reviewClient = reviewClient;
    }

    public List<ReviewDto> getReviewsByBook(Long bookId) {
        return reviewClient.get()
                .uri("/reviews/book/{id}", bookId)
                .retrieve()
                .bodyToFlux(ReviewDto.class)
                .collectList()
                .block(); // bloquea porque tu app NO es reactiva
    }
}