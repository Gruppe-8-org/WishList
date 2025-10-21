package com.wishlist.Repository;

import com.wishlist.Model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class WishlistRepository {
    @Value("${spring.datasource.url}")
    private String dbURL;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    protected final JdbcTemplate jdbcTemplate;
    protected final WishlistRowMapper wishlistRowMapper;

    public WishlistRepository(JdbcTemplate jdbcTemplate, WishlistRowMapper wishlistRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.wishlistRowMapper = wishlistRowMapper;
    }

    public void addWishlist(Wishlist wishlist, List<User> guests) {
        jdbcTemplate.update("INSERT IGNORE INTO Wishlists (AuthorID, Title, HeldOn) VALUES (?, ?, ?);",
                wishlist.getAuthorID(), wishlist.getTitle(), wishlist.getHeldOn());

        for (WishlistProduct wp : wishlist.getProducts()) {
            Product theProduct = wp.getProduct();
            jdbcTemplate.update("INSERT IGNORE INTO Products (Title, Price, Manufacturer, PathToImage) VALUES (?, ?, ?, ?);",
                    theProduct.getTitle(), theProduct.getPrice(), theProduct.getManufacturer(), theProduct.getPathToImage());

            jdbcTemplate.update("INSERT IGNORE INTO WishlistProducts (WishlistID, ProductID, Reserved) VALUES (?, ?, ?);",
                    wishlist.getID(), theProduct.getID(), wp.isReserved());
        }

        for (User user : guests) {
            jdbcTemplate.update("INSERT IGNORE INTO WishlistGuests (WishlistID, UserID) VALUES (?, ?);",
                    wishlist.getID(), user.getID());
        }
    }

    public Wishlist getWishlistByID(int wishlistID) {
        return jdbcTemplate.queryForObject("SELECT Wishlists.ID, Wishlists.Title, Wishlists.AuthorID, Wishlists.HeldOn, Products.Title, Products.Price, Products.Manufacturer, Products.PathToImage, WishlistProducts.Reserved\n" +
                "FROM Wishlists \n" +
                "LEFT JOIN WishlistProducts ON Wishlists.ID = WishlistProducts.WishlistID \n" +
                "LEFT JOIN Products ON WishlistProducts.ProductID = Products.ID\n" +
                "WHERE Wishlists.ID = ?;", Wishlist.class, wishlistID);
    }

    public List<Wishlist> getAllWishlistsByUserWithID(int userID) {
        return jdbcTemplate.queryForList("SELECT Wishlists.ID, Wishlists.Title, Wishlists.AuthorID, Wishlists.HeldOn, Products.Title, Products.Price, Products.Manufacturer, Products.PathToImage, WishlistProducts.Reserved\n" +
                "FROM Wishlists \n" +
                "LEFT JOIN WishlistProducts ON Wishlists.ID = WishlistProducts.WishlistID \n" +
                "LEFT JOIN Products ON WishlistProducts.ProductID = Products.ID\n" +
                "WHERE Wishlists.AuthorID = ?;", Wishlist.class, userID);
    }

    public List<Wishlist> getAllWishlistsUserIsInvitedTo(int userID) {
        return jdbcTemplate.queryForList("SELECT Wishlists.ID, Wishlists.Title, Wishlists.AuthorID, Wishlists.HeldOn, Products.Title, Products.Price, Products.Manufacturer, Products.PathToImage, WishlistProducts.Reserved\n" +
                "FROM Wishlists \n" +
                "JOIN WishlistGuests ON Wishlists.ID = WishlistGuests.WishlistID\n" + // This does the filtering
                "LEFT JOIN WishlistProducts ON Wishlists.ID = WishlistProducts.WishlistID \n" +
                "LEFT JOIN Products ON WishlistProducts.ProductID = Products.ID\n" +
                "WHERE WishlistGuests.UserID = ?;", Wishlist.class, userID);
    }
}
