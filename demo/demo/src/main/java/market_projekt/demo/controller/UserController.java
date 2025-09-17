package market_projekt.demo.controller;

import market_projekt.demo.model.User;
import market_projekt.demo.service.UserService;
import market_projekt.demo.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RatingService ratingService;

    // ✅ Register new user
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    // ✅ Login
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        Optional<User> user = userService.login(username, password);
        return user.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    // ✅ Fetch public profile info (with average rating)
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable String username) {
        Optional<User> user = userService.findByUsername(username);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User u = user.get();

        // Get average rating from RatingService
        double avgRating = ratingService.getAverageRating(u.getId());

        return ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "username", u.getUsername(),
                "email", u.getEmail(),
                "phoneNumber", u.getPhoneNumber() != null ? u.getPhoneNumber() : "Not provided",
                "averageRating", avgRating
        ));
    }

    // ✅ Update phone number
    @PutMapping("/{id}/phone")
    public ResponseEntity<?> updatePhoneNumber(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String phone = body.get("phoneNumber");
        Optional<User> userOpt = userService.findById(id);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOpt.get();
        user.setPhoneNumber(phone);
        userService.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Phone number updated",
                "phoneNumber", user.getPhoneNumber()
        ));
    }
}

