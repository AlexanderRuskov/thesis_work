package market_projekt.demo.service;

import market_projekt.demo.dto.ListingRequest;
import market_projekt.demo.model.Image;
import market_projekt.demo.model.Listing;
import market_projekt.demo.model.User;
import market_projekt.demo.repository.ImageRepository;
import market_projekt.demo.repository.ListingRepository;
import market_projekt.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ListingService {

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ImageService imageService; // ✅ add this

    public List<Listing> getAllListings() {
        return listingRepository.findAll();
    }

    public Optional<Listing> getListingById(Long id) {
        return listingRepository.findById(id);
    }

    public Listing createListing(ListingRequest request) {
        Optional<User> ownerOpt = userRepository.findByUsername(request.owner);
        if (ownerOpt.isEmpty()) {
            throw new RuntimeException("User not found: " + request.owner);
        }
        User owner = ownerOpt.get();

        // Create the listing
        Listing listing = new Listing();
        listing.setTitle(request.title);
        listing.setDescription(request.description);
        listing.setPrice(request.price);
        listing.setOwner(owner);

        listingRepository.save(listing); // Save to get ID

        // Handle image upload
        if (request.image != null && !request.image.isBlank()) {
            Image img = new Image();
            img.setUrl(request.image);
            img.setListing(listing);
            imageRepository.save(img);

            listing.setImages(Collections.singletonList(img)); // Optionally set for response
        }

        return listing;
    }

    public Listing createListingWithImages(
            String title,
            String description,
            Double price,
            String category,
            String city,
            User owner, // not a username anymore
            List<MultipartFile> images
    ) throws IOException {

        // No need to look up owner from DB — it's already passed in
        if (owner == null) {
            throw new RuntimeException("User (owner) is null");
        }

        Listing listing = new Listing();
        listing.setTitle(title);
        listing.setDescription(description);
        listing.setPrice(price);
        listing.setCategory(category);
        listing.setCity(city);
        listing.setOwner(owner); // ✅ set the passed-in owner

        Listing savedListing = listingRepository.save(listing);

        for (MultipartFile file : images) {
            if (!file.isEmpty()) {
                // Validate file type
                String contentType = file.getContentType();
                if (!List.of("image/png", "image/jpeg", "image/webp").contains(contentType)) {
                    throw new RuntimeException("Invalid image type: " + contentType);
                }

                // Simulate image saving (replace this logic with actual save)
                String filename = file.getOriginalFilename(); // Or UUID.randomUUID() + extension
                String imageUrl = "http://localhost:8080/uploads/" + filename;

                Image img = new Image();
                img.setUrl(imageUrl);
                img.setListing(savedListing);

                imageRepository.save(img);
            }
        }

        return savedListing;
    }


    public List<Listing> getListingsByOwner(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(listingRepository::findByOwner).orElse(Collections.emptyList());
    }

    public Listing updateListing(Long id, ListingRequest request) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));

        listing.setTitle(request.title);
        listing.setDescription(request.description);
        listing.setPrice(request.price);
        listing.setCategory(request.category);

        return listingRepository.save(listing);
    }

    public List<Listing> searchListings(String query, String category, String city, Double minPrice, Double maxPrice) {
        return listingRepository.searchListings(
                (query != null && !query.trim().isEmpty()) ? query.trim() : null,
                (category != null && !category.trim().isEmpty()) ? category : "",
                (city != null && !city.trim().isEmpty()) ? city : "",
                minPrice,
                maxPrice
        );
    }


    public void addImagesToListing(Listing listing, List<MultipartFile> images) {
        for (MultipartFile file : images) {
            try {
                Image image = imageService.saveImage(file);
                image.setListing(listing);
                listing.getImages().add(image);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        listingRepository.save(listing);
    }


    public void deleteListing(Long id) {
        listingRepository.deleteById(id);
    }
}

