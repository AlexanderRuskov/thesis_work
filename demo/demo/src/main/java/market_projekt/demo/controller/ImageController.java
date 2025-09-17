package market_projekt.demo.controller;

import market_projekt.demo.model.Image;
import market_projekt.demo.model.Listing;
import market_projekt.demo.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<Image> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Image image = imageService.saveImage(file);
            return ResponseEntity.ok(image);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/upload/{listingId}")
    public ResponseEntity<?> uploadImagesToListing(
            @PathVariable Long listingId,
            @RequestParam("images") java.util.List<MultipartFile> files
    ) {
        try {
            imageService.saveImagesForListing(listingId, files);
            return ResponseEntity.ok(" Images uploaded for listing " + listingId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(" Error uploading images: " + e.getMessage());
        }
    }


}
