package com.wishlist.Service;

import com.wishlist.Exceptions.EntityDoesNotExistException;
import com.wishlist.Exceptions.ZeroRowsAffectedOnUpdateException;
import com.wishlist.Model.Product;
import com.wishlist.Model.User;
import com.wishlist.Model.Wishlist;
import com.wishlist.Repository.ProductRepository;
import com.wishlist.Repository.UserRepository;
import com.wishlist.Repository.WishlistRepository;

import java.util.List;

public class WishlistService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WishlistRepository wishlistRepository;

    public WishlistService(UserRepository userRepository, ProductRepository productRepository, WishlistRepository wishlistRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.wishlistRepository = wishlistRepository;
    }

    public User getUserByID(int userID) {
        return userRepository.getUserByID(userID);
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

    public void userReserveWish(int wishlistID, int productID) {
        if (wishlistRepository.getWishlistByID(wishlistID) == null) {
            throw new EntityDoesNotExistException("Wishlist with ID " + wishlistID + " does not exist.");
        }

        if (productRepository.getProductByID(productID) == null) {
            throw new EntityDoesNotExistException("Product with ID " + productID + " does not exist.");
        }

        userRepository.reserveWish(wishlistID, productID);
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

    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }

    public Product getProductByID(int productID) {
        Product product = productRepository.getProductByID(productID);

        if (product == null) {
            throw new EntityDoesNotExistException("Product with ID " + productID + " does not exist.");
        }

        return product;
    }

    public void updateProduct(Product product) {
        if (productRepository.getProductByID(product.getID()) == null) {
            throw new EntityDoesNotExistException("Product with ID " + product.getID() + " does not exist.");
        }

        int rowsAffected = productRepository.updateProduct(product);
        if (rowsAffected < 1) {
            throw new ZeroRowsAffectedOnUpdateException("Zero rows updated, but product with ID " + product.getID() + " exists");
        }
    }

    public void addProduct(Product product) {
        productRepository.addProduct(product);
    }

    public void addProducts(List<Product> products) {
        productRepository.addProducts(products);
    }

    public void deleteProductByID(int productID) {
        if (productRepository.getProductByID(productID) == null) {
            throw new EntityDoesNotExistException("Product with ID " + productID + " does not exist.");
        }

        int rowsAffected = productRepository.deleteProductByID(productID);
        if (rowsAffected < 1) {
            throw new ZeroRowsAffectedOnUpdateException("Zero rows deleted, but product with ID " + productID + " exists");
        }
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

    public boolean login(int userID, String password) {
        User user = userRepository.getUserByID(userID);

        if (user != null)
            return user.getPassword().equals(password);

        return false;
    }
}
