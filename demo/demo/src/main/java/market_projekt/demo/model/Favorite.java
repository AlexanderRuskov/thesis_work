package market_projekt.demo.model;

import jakarta.persistence.*;

@Entity
@IdClass(FavoriteId.class)
@Table(name = "favorites")
public class Favorite {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "listing_id")
    private Long listingId;

    public Favorite() {}

    public Favorite(Long userId, Long listingId) {
        this.userId = userId;
        this.listingId = listingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }
}
