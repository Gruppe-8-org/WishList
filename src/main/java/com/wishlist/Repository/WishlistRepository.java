package com.wishlist.Repository;

import com.wishlist.Model.*;
import com.wishlist.RowMappers.ProductRowMapper;
import com.wishlist.RowMappers.WishlistProductRowMapper;
import com.wishlist.RowMappers.WishlistRowMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
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

    //UDKOMMENTERET og tilføjet en ny for at se om det får metoden til at virke
    //Har ladet den blive hvis det giver bedre mening at bruge den senere og i stedet ændre controller/service
//    public void addWishlist(Wishlist wishlist, List<User> guests) {
//        jdbcTemplate.update("INSERT IGNORE INTO Wishlists (AuthorID, WishlistTitle, HeldOn) VALUES (?, ?, ?);",
//                wishlist.getAuthorID(), wishlist.getTitle(), wishlist.getHeldOn());
//
//        for (WishlistProduct wp : wishlist.getProducts()) {
//            Product theProduct = wp.getProduct();
//            jdbcTemplate.update("INSERT IGNORE INTO Products (ProductTitle, ProductPrice, ProductManufacturer, ProductPathToImage) VALUES (?, ?, ?, ?);",
//                    theProduct.getTitle(), theProduct.getPrice(), theProduct.getManufacturer(), theProduct.getPathToImage());
//
//            jdbcTemplate.update("INSERT IGNORE INTO WishlistProducts (WishlistID, ProductID, Reserved) VALUES (?, ?, ?);",
//                    wishlist.getID(), theProduct.getID(), wp.isReserved());
//        }
//
//        for (User user : guests) {
//            jdbcTemplate.update("INSERT IGNORE INTO WishlistGuests (WishlistID, UserID) VALUES (?, ?);",
//                    wishlist.getID(), user.getID());
//        }
//    }
public void addWishlist(Wishlist wishlist, List<User> guests) {
    String sql = "INSERT INTO Wishlists (AuthorID, WishlistTitle, HeldOn) VALUES (?, ?, ?)";

    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, wishlist.getAuthorID());
        ps.setString(2, wishlist.getTitle());
        ps.setObject(3, wishlist.getHeldOn()); // assuming HeldOn can be null
        return ps;
    }, keyHolder);

    // set the generated ID back to the wishlist
    wishlist.setID(keyHolder.getKey().intValue());

    // Now you can safely insert products
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
