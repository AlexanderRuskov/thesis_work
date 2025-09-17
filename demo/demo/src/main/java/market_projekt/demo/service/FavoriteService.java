package market_projekt.demo.service;

import market_projekt.demo.model.Favorite;
import market_projekt.demo.model.FavoriteId;
import market_projekt.demo.model.Listing;
import market_projekt.demo.repository.FavoriteRepository;
import market_projekt.demo.repository.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ListingRepository listingRepository;

    public Favorite addFavorite(Long userId, Long listingId) {
        Favorite fav = new Favorite(userId, listingId);
        return favoriteRepository.save(fav);
    }

    public void removeFavorite(Long userId, Long listingId) {
        favoriteRepository.deleteById(new FavoriteId(userId, listingId));
    }

    public List<Listing> getFavorites(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        // map each favorite to its listing (if it exists)
        return favorites.stream()
                .map(fav -> listingRepository.findById(fav.getListingId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
