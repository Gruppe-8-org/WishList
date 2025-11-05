package com.wishlist.Service;

import com.wishlist.Exceptions.EntityAlreadyExistsException;
import com.wishlist.Exceptions.EntityDoesNotExistException;
import com.wishlist.Exceptions.OperationNotAllowedException;
import com.wishlist.Exceptions.ZeroRowsAffectedOnUpdateException;
import com.wishlist.Model.User;
import com.wishlist.Repository.ProductRepository;
import com.wishlist.Repository.UserRepository;
import com.wishlist.Repository.WishlistRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
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

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public int addUser(User user) {
        if (userRepository.getUserByUsername(user.getUsername()) != null) {
            throw new EntityAlreadyExistsException("User with username " + user.getUsername() + " already exists.");
        }

        return userRepository.addUser(user);
    }

    public int updateUser(User user) {
        if (userRepository.getUserByID(user.getID()) == null) {
            throw new EntityDoesNotExistException("User with ID " + user.getID() + " does not exist.");
        }

        int rowsAffected = userRepository.updateUser(user);
        if (rowsAffected < 1) {
            throw new ZeroRowsAffectedOnUpdateException("Zero rows updated, but user with ID " + user.getID() + " exists");
        }

        return rowsAffected;
    }

    public int deleteUserByID(int userID) {
        if (userRepository.getUserByID(userID) == null) {
            throw new EntityDoesNotExistException("User with ID " + userID + " does not exist.");
        }

        int rowsAffected = userRepository.deleteUserByID(userID);
        if (rowsAffected < 1) {
            throw new ZeroRowsAffectedOnUpdateException("Zero rows deleted, but user with ID " + userID + " exists");
        }

        return rowsAffected;
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

    public int userReserveWish(int reserverID, int wishlistID, int productID) {
        if (wishlistRepository.getWishlistByID(wishlistID) == null) {
            throw new EntityDoesNotExistException("Wishlist with ID " + wishlistID + " does not exist.");
        }

        if (productRepository.getProductByID(productID) == null) {
            throw new EntityDoesNotExistException("Product with ID " + productID + " does not exist.");
        }

        if (getReserverID(wishlistID, productID) != reserverID && getReserverID(wishlistID, productID) != 0) {
            throw new OperationNotAllowedException("You cannot reserve something already reserved by another user.");
        }

        int rowsAffected = userRepository.reserveWish(reserverID, wishlistID, productID);

        if (rowsAffected < 1) {
            throw new ZeroRowsAffectedOnUpdateException("Couldn't reserve wish with WishlistID " + wishlistID + ", productID " + productID + ", but such a WishlistProduct exists");
        }

        return rowsAffected;
    }

    public int userUnreserveWish(int unreserverID, int wishlistID, int productID) {
        if (wishlistRepository.getWishlistByID(wishlistID) == null) {
            throw new EntityDoesNotExistException("Wishlist with ID " + wishlistID + " does not exist.");
        }

        if (productRepository.getProductByID(productID) == null) {
            throw new EntityDoesNotExistException("Product with ID " + productID + " does not exist.");
        }

        if (getReserverID(wishlistID, productID) == 0) {
            throw new OperationNotAllowedException("You cannot unreserve something not reserved.");
        }

        if (getReserverID(wishlistID, productID) != unreserverID) {
            throw new OperationNotAllowedException("You cannot unreserve something reserved by another user.");
        }

        int rowsAffected = userRepository.unreserveWish(wishlistID, productID);

        if (rowsAffected < 1) {
            throw new ZeroRowsAffectedOnUpdateException("Couldn't reserve wish with WishlistID " + wishlistID + ", productID " + productID + ", but such a WishlistProduct exists");
        }

        return rowsAffected;
    }

    public boolean login(String username, String password) {
        User user = userRepository.getUserByUsername(username);

        if (user != null)
            return user.getPassword().equals(password);

        return false;
    }

    public int getReserverID(int wishlistID, int productID) {
        if (wishlistRepository.getWishlistByID(wishlistID) == null) {
            throw new EntityDoesNotExistException("Wishlist with ID " + wishlistID + " does not exist.");
        }

        if (productRepository.getProductByID(productID) == null) {
            throw new EntityDoesNotExistException("Product with ID " + productID + " does not exist.");
        }

        return userRepository.getReserverID(wishlistID, productID);
    }

    public User getUserByUsername (String username) {
        User user = userRepository.getUserByUsername(username);

        if (user == null) {
            throw new EntityDoesNotExistException("User with Username " + username + " does not exist.");
        }

        return user;
    }
}
