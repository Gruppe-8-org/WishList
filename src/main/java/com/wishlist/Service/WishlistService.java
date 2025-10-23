package com.wishlist.Service;

import com.wishlist.Model.User;
import com.wishlist.Model.Wishlist;
import com.wishlist.Repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {
    @Autowired
    public WishlistRepository wishlistRepository;


    public void addWishlist(Wishlist wishlist, List<User> guests) {
        wishlistRepository.addWishlist(wishlist, guests);

    }
    public Wishlist getWishlistByID(int id) {
        return wishlistRepository.getWishlistByID(id);
    }
    public void deleteWishlistByID(int id) {
        wishlistRepository.deleteWishlistByID(id);
    }
}
