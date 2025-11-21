package minzbook.reviewservice.repository;

import minzbook.reviewservice.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBookIdAndActivoTrue(Long bookId);

    List<Review> findByUserIdAndActivoTrue(Long userId);
}
