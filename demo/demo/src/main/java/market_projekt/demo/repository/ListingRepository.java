package market_projekt.demo.repository;

import market_projekt.demo.model.Listing;
import market_projekt.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByOwner(User owner);

    // 🔹 Basic filter
    @Query("SELECT l FROM Listing l " +
            "WHERE (:category IS NULL OR :category = '' OR l.category = :category) " +
            "AND (:city IS NULL OR :city = '' OR LOWER(l.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
            "AND (:minPrice IS NULL OR l.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR l.price <= :maxPrice)")
    List<Listing> findByFilters(
            @Param("category") String category,
            @Param("city") String city,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );

    // 🔹 Search
    @Query("SELECT l FROM Listing l " +
            "WHERE ((:query IS NULL OR :query = '') " +
            "   OR LOWER(l.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "   OR LOWER(l.description) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND (:category IS NULL OR :category = '' OR l.category = :category) " +
            "AND (:city IS NULL OR :city = '' OR LOWER(l.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
            "AND (:minPrice IS NULL OR l.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR l.price <= :maxPrice)")
    List<Listing> searchListings(
            @Param("query") String query,
            @Param("category") String category,
            @Param("city") String city,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );

}

