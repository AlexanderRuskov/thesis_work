package market_projekt.demo.controller;

import market_projekt.demo.model.User;
import market_projekt.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists.");
        }

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password.");
        }

        return ResponseEntity.ok(userOpt.get());
    }
}
