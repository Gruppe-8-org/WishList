package com.wishlist.Service;

import com.wishlist.Exceptions.EntityDoesNotExistException;
import com.wishlist.Exceptions.ZeroRowsAffectedOnUpdateException;
import com.wishlist.Model.User;
import com.wishlist.Repository.ProductRepository;
import com.wishlist.Repository.UserRepository;
import com.wishlist.Repository.WishlistRepository;

public class UserService {
    private final UserRepository userRepository;
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public UserService(UserRepository userRepository, WishlistRepository wishlistRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
    }

    public User getUserByID(int userID) {
        User user = userRepository.getUserByID(userID);

        if (user == null) {
            throw new EntityDoesNotExistException("User with ID " + userID + " does not exist.");
        }

        return user;
    }

    public void addUser(User user) {
        // Discarding return value because user may already exist, and INSERT is ignored
        userRepository.addUser(user);
    }

    public void updateUser(User user) {
        if (userRepository.getUserByID(user.getID()) == null) {
            throw new EntityDoesNotExistException("User with ID " + user.getID() + " does not exist.");
        }

        int rowsAffected = userRepository.updateUser(user);
        if (rowsAffected < 1) {
            throw new ZeroRowsAffectedOnUpdateException("Zero rows updated, but user with ID " + user.getID() + " exists");
        }
    }

    public void deleteUserByID(int userID) {
        if (userRepository.getUserByID(userID) == null) {
            throw new EntityDoesNotExistException("User with ID " + userID + " does not exist.");
        }

        int rowsAffected = userRepository.deleteUserByID(userID);
        if (rowsAffected < 1) {
            throw new ZeroRowsAffectedOnUpdateException("Zero rows deleted, but user with ID " + userID + " exists");
        }
    }

    public boolean userCanViewWishList(int wishlistID, int userID) {
        if (wishlistRepository.getWishlistByID(wishlistID) == null) {
            throw new EntityDoesNotExistException("Wishlist with ID " + wishlistID + " does not exist.");
        }

        if (userRepository.getUserByID(userID) == null) {
            throw new EntityDoesNotExistException("User with ID " + userID + " does not exist.");
        }

        return userRepository.canViewWishlist(wishlistID, userID);
    }

    public void userReserveWish(int reserverID, int wishlistID, int productID) {
        if (wishlistRepository.getWishlistByID(wishlistID) == null) {
            throw new EntityDoesNotExistException("Wishlist with ID " + wishlistID + " does not exist.");
        }

        if (productRepository.getProductByID(productID) == null) {
            throw new EntityDoesNotExistException("Product with ID " + productID + " does not exist.");
        }

        userRepository.reserveWish(reserverID, wishlistID, productID);
    }

    public void userUnreserveWish(int wishlistID, int productID) {
        if (wishlistRepository.getWishlistByID(wishlistID) == null) {
            throw new EntityDoesNotExistException("Wishlist with ID " + wishlistID + " does not exist.");
        }

        if (productRepository.getProductByID(productID) == null) {
            throw new EntityDoesNotExistException("Product with ID " + productID + " does not exist.");
        }

        userRepository.unreserveWish(wishlistID, productID);
    }

    public boolean login(int userID, String password) {
        User user = userRepository.getUserByID(userID);

        if (user != null)
            return user.getPassword().equals(password);

        return false;
    }
}
