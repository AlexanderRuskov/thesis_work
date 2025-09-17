package market_projekt.demo.controller;

import market_projekt.demo.model.Rating;
import market_projekt.demo.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    // ✅ Add a rating
    @PostMapping("/add")
    public ResponseEntity<Rating> addRating(@RequestBody Map<String, Object> body) {
        Long raterId = Long.valueOf(body.get("raterId").toString());
        Long ratedUserId = Long.valueOf(body.get("ratedUserId").toString());
        int score = Integer.parseInt(body.get("score").toString());
        String comment = (String) body.get("comment");

        Rating rating = ratingService.addRating(raterId, ratedUserId, score, comment);
        return ResponseEntity.ok(rating);
    }

    // ✅ Get all ratings for a user
    @GetMapping("/{userId}")
    public ResponseEntity<List<Rating>> getRatingsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ratingService.getRatingsForUser(userId));
    }

    // ✅ Get average rating for a user
    @GetMapping("/{userId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long userId) {
        return ResponseEntity.ok(ratingService.getAverageRating(userId));
    }
}
