package market_projekt.demo.controller;


import market_projekt.demo.model.Listing;
import market_projekt.demo.model.User;
import market_projekt.demo.service.ListingService;
import market_projekt.demo.dto.ListingRequest;
import market_projekt.demo.repository.UserRepository;
import market_projekt.demo.repository.ListingRepository;

import java.security.Principal;
import java.util.Collections;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    @Autowired
    private ListingService listingService;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Listing> getAll() {
        return listingService.getAllListings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Listing> getById(@PathVariable Long id) {
        Optional<Listing> optionalListing = listingRepository.findById(id);
        if (optionalListing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Listing listing = optionalListing.get();
        listing.setViews(listing.getViews() + 1); // Increment view count
        listingRepository.save(listing);          // Save updated listing

        return ResponseEntity.ok(listing);
    }

    @GetMapping("/user/{username}")
    public List<Listing> getByOwner(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(listingRepository::findByOwner).orElse(Collections.emptyList());
    }

    @GetMapping("/search")
    public List<Listing> searchListings(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        return listingService.searchListings(query, category, city, minPrice, maxPrice);
    }


    @GetMapping("/filter")
    public List<Listing> filterListings(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        return listingRepository.findByFilters(
                (category != null && !"all".equalsIgnoreCase(category)) ? category : "",
                (city != null) ? city : "",
                minPrice,
                maxPrice
        );
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ListingRequest request, Principal principal) {
        try {
            // Get the logged-in user
            String username = principal.getName();
            Optional<User> user = userRepository.findByUsername(username);

            if (user.isEmpty()) {
                return ResponseEntity.badRequest().body("❌ Invalid user.");
            }

            Listing listing = new Listing();
            listing.setTitle(request.title);
            listing.setDescription(request.description);
            listing.setPrice(request.price);
            listing.setCategory(request.category);
            listing.setCity(request.city);
            listing.setOwner(user.get()); // ✅ Set the owner here

            Listing saved = listingRepository.save(listing); // Use repository directly or through service
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Error: " + e.getMessage());
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadListingWithImages(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam String category,
            @RequestParam String city, // 🆕 Add this
            @RequestParam String owner, // ✅ Accept owner username from frontend
            @RequestParam("images") List<MultipartFile> images
    ) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(owner);

            if (userOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("❌ Invalid user.");
            }

            User ownerUser = userOptional.get();

            // ✅ Create the listing via service
            Listing listing = listingService.createListingWithImages(
                    title,
                    description,
                    price,
                    category,
                    city,
                    ownerUser, // Now pass the actual User entity
                    images
            );

            return ResponseEntity.ok(listing);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Error: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<?> uploadImagesForListing(
            @PathVariable Long id,
            @RequestParam("images") List<MultipartFile> images) {

        Optional<Listing> listingOpt = listingRepository.findById(id);
        if (listingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Listing listing = listingOpt.get();
        listingService.addImagesToListing(listing, images);
        return ResponseEntity.ok(listing);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateListing(@PathVariable Long id, @RequestBody ListingRequest request) {
        try {
            Listing updated = listingService.updateListing(id, request);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("❌ Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        listingService.deleteListing(id);
    }

    @DeleteMapping("/{listingId}/images/{imageId}")
    public ResponseEntity<?> deleteImageFromListing(
            @PathVariable Long listingId,
            @PathVariable Long imageId) {

        Optional<Listing> optionalListing = listingRepository.findById(listingId);
        if (optionalListing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Listing listing = optionalListing.get();

        boolean removed = listing.getImages().removeIf(img -> img.getId().equals(imageId));
        if (!removed) {
            return ResponseEntity.badRequest().body("❌ Image not found in this listing.");
        }

        listingRepository.save(listing);
        return ResponseEntity.ok("✅ Image deleted successfully!");
    }

}
