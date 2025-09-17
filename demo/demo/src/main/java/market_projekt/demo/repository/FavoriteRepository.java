package market_projekt.demo.repository;

import market_projekt.demo.model.Favorite;
import market_projekt.demo.model.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    List<Favorite> findByUserId(Long userId);
}
