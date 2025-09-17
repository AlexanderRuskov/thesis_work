package market_projekt.demo.service;

import market_projekt.demo.model.Rating;
import market_projekt.demo.model.User;
import market_projekt.demo.repository.RatingRepository;
import market_projekt.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ Add a new rating
    public Rating addRating(Long raterId, Long ratedUserId, int score, String comment) {
        Optional<User> rater = userRepository.findById(raterId);
        Optional<User> ratedUser = userRepository.findById(ratedUserId);

        if (rater.isEmpty() || ratedUser.isEmpty()) {
            throw new RuntimeException("Invalid user(s) for rating");
        }

        Rating rating = new Rating(rater.get(), ratedUser.get(), score, comment);
        return ratingRepository.save(rating);
    }

    // ✅ Get all ratings for a user
    public List<Rating> getRatingsForUser(Long ratedUserId) {
        return ratingRepository.findByRatedUserId(ratedUserId);
    }

    // ✅ Calculate average rating
    public double getAverageRating(Long ratedUserId) {
        List<Rating> ratings = ratingRepository.findByRatedUserId(ratedUserId);
        if (ratings.isEmpty()) return 0.0;

        return ratings.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);
    }
}
