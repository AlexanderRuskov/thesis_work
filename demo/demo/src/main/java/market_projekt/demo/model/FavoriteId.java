package market_projekt.demo.model;

import java.io.Serializable;
import java.util.Objects;

public class FavoriteId implements Serializable {
    private Long userId;
    private Long listingId;

    public FavoriteId() {}

    public FavoriteId(Long userId, Long listingId) {
        this.userId = userId;
        this.listingId = listingId;
    }

    //  equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavoriteId)) return false;
        FavoriteId that = (FavoriteId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(listingId, that.listingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, listingId);
    }
}
