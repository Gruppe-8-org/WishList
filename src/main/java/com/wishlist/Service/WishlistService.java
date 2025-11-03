package com.wishlist.Service;

import com.wishlist.Exceptions.EntityDoesNotExistException;
import com.wishlist.Exceptions.ZeroRowsAffectedOnUpdateException;
import com.wishlist.Model.User;
import com.wishlist.Model.Wishlist;
import com.wishlist.Repository.UserRepository;
import com.wishlist.Repository.WishlistRepository;

import java.util.List;

public class WishlistService {
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;

    public WishlistService(UserRepository userRepository, WishlistRepository wishlistRepository) {
        this.userRepository = userRepository;
        this.wishlistRepository = wishlistRepository;
    }

    public void addWishlist(Wishlist wishlist, List<User> guests) {
        wishlistRepository.addWishlist(wishlist, guests);
    }

    public void updateWishlist(Wishlist wishlist, List<User> guests) {
        if (wishlistRepository.getWishlistByID(wishlist.getID()) == null) {
            throw new EntityDoesNotExistException("Wishlist with ID " + wishlist.getID() + " does not exist.");
        }

        wishlistRepository.updateWishlist(wishlist, guests);
    }

    public Wishlist getWishlistByID(int wishlistID) {
        Wishlist wishlist = wishlistRepository.getWishlistByID(wishlistID);

        if (wishlist == null) {
            throw new EntityDoesNotExistException("Wishlist with ID " + wishlistID + " does not exist.");
        }

        return wishlist;
    }

    public List<Wishlist> getAllWishlistsByUserWithID(int userID) {
        if (userRepository.getUserByID(userID) == null) {
            throw new EntityDoesNotExistException("User with ID " + userID + " does not exist.");
        }

        return wishlistRepository.getAllWishlistsByUserWithID(userID);
    }

    public List<Wishlist> getAllWishlistsUserIsInvitedTo(int userID) {
        if (userRepository.getUserByID(userID) == null) {
            throw new EntityDoesNotExistException("User with ID " + userID + " does not exist.");
        }

        return wishlistRepository.getAllWishlistsUserIsInvitedTo(userID);
    }

    public void deleteWishlistByID(int wishlistID) {
        if (wishlistRepository.getWishlistByID(wishlistID) == null) {
            throw new EntityDoesNotExistException("Wishlist with ID " + wishlistID + " does not exist.");
        }

        int rowsAffected = wishlistRepository.deleteWishlistByID(wishlistID);
        if (rowsAffected < 1) {
            throw new ZeroRowsAffectedOnUpdateException("Zero rows deleted, but wishlist with ID " + wishlistID + " exists.");
        }
    }

    public void deleteAllWishlistsByUser(int userID) {
        if (userRepository.getUserByID(userID) == null) {
            throw new EntityDoesNotExistException("User with ID " + userID + " does not exist.");
        }

        int rowsAffected = wishlistRepository.deleteWishlistsByUser(userID);
        if (rowsAffected < 1) {
            throw new ZeroRowsAffectedOnUpdateException("Zero rows deleted, but user with ID " + userID + " exists.");
        }
    }

}
