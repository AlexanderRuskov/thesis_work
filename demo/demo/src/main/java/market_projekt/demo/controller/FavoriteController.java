package market_projekt.demo.controller;

import market_projekt.demo.model.Favorite;
import market_projekt.demo.model.Listing;
import market_projekt.demo.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    // Add a favorite
    @PostMapping("/add")
    public ResponseEntity<Favorite> addFavorite(@RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        Long listingId = body.get("listingId");
        return ResponseEntity.ok(favoriteService.addFavorite(userId, listingId));
    }

    //  Remove a favorite
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFavorite(@RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        Long listingId = body.get("listingId");
        favoriteService.removeFavorite(userId, listingId);
        return ResponseEntity.ok(Map.of("message", "Removed from favorites"));
    }

    //  Get all favorites for a user (returns full listings now)
    @GetMapping("/{userId}")
    public ResponseEntity<List<Listing>> getFavorites(@PathVariable Long userId) {
        return ResponseEntity.ok(favoriteService.getFavorites(userId));
    }
}
