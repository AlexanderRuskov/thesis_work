package market_projekt.demo.service;

import jakarta.annotation.PostConstruct;
import market_projekt.demo.model.Image;
import market_projekt.demo.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final market_projekt.demo.repository.ListingRepository listingRepository; // ✅ inject ListingRepository

    @Value("${upload.dir}")
    private String uploadDir;

    public ImageService(ImageRepository imageRepository,
                        market_projekt.demo.repository.ListingRepository listingRepository) {
        this.imageRepository = imageRepository;
        this.listingRepository = listingRepository;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!", e);
        }
    }

    public Image saveImage(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";

        String newFileName = UUID.randomUUID() + extension;

        // Save the file to disk
        Path filePath = Paths.get(uploadDir, newFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Save just the relative URL
        Image image = new Image();
        image.setUrl("/uploads/" + newFileName);
        return imageRepository.save(image);
    }

    // method: save images for a listing
    public void saveImagesForListing(Long listingId, java.util.List<MultipartFile> files) throws IOException {
        var listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found with id: " + listingId));

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";

            String newFileName = UUID.randomUUID() + extension;
            Path filePath = Paths.get(uploadDir, newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Image image = new Image();
            image.setUrl("/uploads/" + newFileName);
            image.setListing(listing); // ✅ associate with listing
            imageRepository.save(image);
        }
    }
}
