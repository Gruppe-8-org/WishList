package com.wishlist.Repository;

import com.wishlist.Model.*;
import com.wishlist.RowMappers.ProductRowMapper;
import com.wishlist.RowMappers.WishlistProductRowMapper;
import com.wishlist.RowMappers.WishlistRowMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WishlistRepository {
    @Value("${spring.datasource.url}")
    private String dbURL;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    protected final JdbcTemplate jdbcTemplate;
    protected final WishlistRowMapper wishlistRowMapper;

    public WishlistRepository(JdbcTemplate jdbcTemplate, WishlistRowMapper wishlistRowMapper, WishlistProductRowMapper wishlistProductRowMapper, ProductRowMapper productRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.wishlistRowMapper = wishlistRowMapper;
    }

    public void addWishlist(Wishlist wishlist, List<User> guests) {
        jdbcTemplate.update("INSERT IGNORE INTO Wishlists (AuthorID, WishlistTitle, HeldOn) VALUES (?, ?, ?);",
                wishlist.getAuthorID(), wishlist.getTitle(), wishlist.getHeldOn());

        for (WishlistProduct wp : wishlist.getProducts()) {
            Product theProduct = wp.getProduct();
            jdbcTemplate.update("INSERT IGNORE INTO Products (ProductTitle, ProductPrice, ProductManufacturer, ProductPathToImage) VALUES (?, ?, ?, ?);",
                    theProduct.getTitle(), theProduct.getPrice(), theProduct.getManufacturer(), theProduct.getPathToImage());

            jdbcTemplate.update("INSERT IGNORE INTO WishlistProducts (WishlistID, ProductID, Reserved) VALUES (?, ?, ?);",
                    wishlist.getID(), theProduct.getID(), wp.isReserved());
        }

        for (User user : guests) {
            jdbcTemplate.update("INSERT IGNORE INTO WishlistGuests (WishlistID, UserID) VALUES (?, ?);",
                    wishlist.getID(), user.getID());
        }
    }

    public void updateWishlist(Wishlist editedWishlist, List<User> guests) {
        jdbcTemplate.update("UPDATE Wishlists SET AuthorID = ?, WishlistTitle = ?, HeldOn = ? WHERE WishlistID = ?",
                editedWishlist.getAuthorID(), editedWishlist.getTitle(), editedWishlist.getHeldOn(), editedWishlist.getID());

        jdbcTemplate.update("DELETE FROM WishlistProducts WHERE WishlistID = ?", editedWishlist.getID());
        jdbcTemplate.update("DELETE FROM WishlistGuests WHERE WishlistID = ?", editedWishlist.getID());

        for (WishlistProduct wp : editedWishlist.getProducts()) {
            int productID = wp.getProduct().getID();
            boolean reserved = wp.isReserved();

            jdbcTemplate.update("INSERT IGNORE INTO WishlistProducts (WishlistID, ProductID, Reserved) VALUES (?, ?, ?)",
                    editedWishlist.getID(), productID, reserved);
        }

        for (User guest : guests) {
            jdbcTemplate.update("INSERT IGNORE INTO WishlistGuests (WishlistID, UserID) VALUES (?, ?)",
                    editedWishlist.getID(), guest.getID());
        }
    }

    public Wishlist getWishlistByID(int wishlistID) {
        return jdbcTemplate.queryForObject("SELECT Wishlists.WishlistID, Wishlists.WishlistTitle, Wishlists.AuthorID, Wishlists.HeldOn, Products.ProductID, ProductTitle, ProductPrice, ProductManufacturer, ProductPathToImage, WishlistProducts.Reserved\n" +
                "FROM Wishlists \n" +
                "LEFT JOIN WishlistProducts ON Wishlists.WishlistID = WishlistProducts.WishlistID \n" +
                "LEFT JOIN Products ON WishlistProducts.ProductID = Products.ProductID\n" +
                "WHERE Wishlists.WishlistID = ?;", wishlistRowMapper, wishlistID);
    }

    public List<Wishlist> getAllWishlistsByUserWithID(int userID) {
        return jdbcTemplate.query("SELECT Wishlists.WishlistID, Wishlists.WishlistTitle, Wishlists.AuthorID, Wishlists.HeldOn, Products.ProductID, ProductTitle, ProductPrice, ProductManufacturer, ProductPathToImage, WishlistProducts.Reserved\n" +
                "FROM Wishlists \n" +
                "LEFT JOIN WishlistProducts ON Wishlists.WishlistID = WishlistProducts.WishlistID \n" +
                "LEFT JOIN Products ON WishlistProducts.ProductID = Products.ProductID\n" +
                "WHERE Wishlists.AuthorID = ?;", wishlistRowMapper, userID);
    }

    public List<Wishlist> getAllWishlistsUserIsInvitedTo(int userID) {
        return jdbcTemplate.query("SELECT Wishlists.WishlistID, Wishlists.WishlistTitle, Wishlists.AuthorID, Wishlists.HeldOn, Products.ProductID, ProductTitle, ProductPrice, ProductManufacturer, ProductPathToImage, WishlistProducts.Reserved\n" +
                "FROM Wishlists \n" +
                "JOIN WishlistGuests ON Wishlists.WishlistID = WishlistGuests.WishlistID\n" +
                "LEFT JOIN WishlistProducts ON Wishlists.WishlistID = WishlistProducts.WishlistID \n" +
                "LEFT JOIN Products ON WishlistProducts.ProductID = Products.ProductID\n" +
                "WHERE WishlistGuests.UserID = ?;", wishlistRowMapper, userID);
    }

    // ToDo: Verify that cascading works as expected
    // (Entries in corresponding junction tables should also be removed)
    public int deleteWishlistByID(int wishlistID) {
        return jdbcTemplate.update("DELETE FROM Wishlists WHERE WishlistID = ?;", wishlistID);
    }

    // ToDo: Verify that cascading works as expected
    // (Entries in corresponding junction tables should also be removed)
    public int deleteWishlistsByUser(int userID) {
        return jdbcTemplate.update("DELETE FROM Wishlists WHERE AuthorID = ?", userID);
    }
}
