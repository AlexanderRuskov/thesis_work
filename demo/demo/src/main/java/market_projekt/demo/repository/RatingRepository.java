package market_projekt.demo.repository;

import market_projekt.demo.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRatedUserId(Long ratedUserId);
}
