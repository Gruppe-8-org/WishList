package com.wishlist.Repository;

import com.wishlist.Model.*;
import com.wishlist.RowMappers.WishlistRowMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.ZoneId;
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

    public WishlistRepository(JdbcTemplate jdbcTemplate, WishlistRowMapper wishlistRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.wishlistRowMapper = wishlistRowMapper;
    }

    @Transactional
    public void addWishlist(Wishlist wishlist, List<User> guests) {
        String sql = "INSERT INTO Wishlists (AuthorID, WishlistTitle, HeldOn) VALUES (?, ?, ?);";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, wishlist.getAuthorID());
            ps.setString(2, wishlist.getTitle());
            ps.setDate(3, java.sql.Date.valueOf(wishlist.getHeldOn()));
            return ps;
        }, keyHolder);

        Number wishlistIdNum = keyHolder.getKey();
        if (wishlistIdNum == null) throw new RuntimeException("Failed to get Wishlist ID");
        int wishlistId = wishlistIdNum.intValue();

        for (WishlistProduct wp : wishlist.getProducts()) {
            Product theProduct = wp.getProduct();
            jdbcTemplate.update(
                    "INSERT IGNORE INTO Products (ProductID, ProductTitle, ProductPrice, ProductManufacturer, ProductPathToImage) VALUES (?, ?, ?, ?, ?);",
                    theProduct.getID(), theProduct.getTitle(), theProduct.getPrice(), theProduct.getManufacturer(), theProduct.getPathToImage()
            );

            jdbcTemplate.update(
                    "INSERT INTO WishlistProducts (WishlistID, ProductID, Description, ReservedBy) VALUES (?, ?, ?, ?);",
                    wishlistId, theProduct.getID(), wp.getDescription(), wp.getReserverID() == 0 ? null : wp.getReserverID()
            );
        }

        for (User user : guests) {
            jdbcTemplate.update("INSERT INTO WishlistGuests (WishlistID, UserID) VALUES (?, ?);", wishlistId, user.getID());
        }
    }


    @Transactional
    public void updateWishlist(Wishlist editedWishlist, List<User> guests) {
        jdbcTemplate.update("UPDATE Wishlists SET AuthorID = ?, WishlistTitle = ?, HeldOn = ? WHERE WishlistID = ?;",
                editedWishlist.getAuthorID(), editedWishlist.getTitle(), editedWishlist.getHeldOn(), editedWishlist.getID());

        jdbcTemplate.update("DELETE FROM WishlistProducts WHERE WishlistID = ?;", editedWishlist.getID());
        jdbcTemplate.update("DELETE FROM WishlistGuests WHERE WishlistID = ?;", editedWishlist.getID());

        for (WishlistProduct wp : editedWishlist.getProducts()) {
            Product theProduct = wp.getProduct();

            jdbcTemplate.update("INSERT IGNORE INTO Products (ProductID, ProductTitle, ProductManufacturer, ProductPathToImage, ProductPrice) VALUES (?, ?, ?, ?, ?);",
                    theProduct.getID(), theProduct.getTitle(), theProduct.getManufacturer(), theProduct.getManufacturer(), theProduct.getPrice());

            jdbcTemplate.update("INSERT IGNORE INTO WishlistProducts (WishlistID, ProductID, Description, ReservedBy) VALUES (?, ?, ?, ?);",
                    editedWishlist.getID(), theProduct.getID(), wp.getDescription(), wp.getReserverID() == 0 ? null : wp.getReserverID());
        }

        for (User guest : guests) {
            jdbcTemplate.update("INSERT IGNORE INTO WishlistGuests (WishlistID, UserID) VALUES (?, ?);",
                    editedWishlist.getID(), guest.getID());
        }
    }

    public Wishlist getWishlistByID(int wishlistID) {
        Wishlist wishlistToReturn;

        try {
            wishlistToReturn = jdbcTemplate.queryForObject("""
                    SELECT Wishlists.WishlistID, Wishlists.WishlistTitle, Wishlists.AuthorID, Wishlists.HeldOn, Products.ProductID, ProductTitle, ProductPrice, ProductManufacturer, ProductPathToImage, WishlistProducts.ReservedBy, WishlistProducts.Description
                    FROM Wishlists\s
                    LEFT JOIN WishlistProducts ON Wishlists.WishlistID = WishlistProducts.WishlistID\s
                    LEFT JOIN Products ON WishlistProducts.ProductID = Products.ProductID
                    WHERE Wishlists.WishlistID = ?;""", wishlistRowMapper, wishlistID);
        } catch (EmptyResultDataAccessException erdae) {
            wishlistToReturn = null;
        }

        return wishlistToReturn;
    }

    public List<Wishlist> getAllWishlistsByUserWithID(int userID) {
        return jdbcTemplate.query("""
                SELECT Wishlists.WishlistID, Wishlists.WishlistTitle, Wishlists.AuthorID, Wishlists.HeldOn, Products.ProductID, ProductTitle, ProductPrice, ProductManufacturer, ProductPathToImage, WishlistProducts.ReservedBy, WishlistProducts.Description
                FROM Wishlists\s
                LEFT JOIN WishlistProducts ON Wishlists.WishlistID = WishlistProducts.WishlistID\s
                LEFT JOIN Products ON WishlistProducts.ProductID = Products.ProductID
                WHERE Wishlists.AuthorID = ?;""", wishlistRowMapper, userID);
    }

    public List<Wishlist> getAllWishlistsUserIsInvitedTo(int userID) {
        return jdbcTemplate.query("""
                SELECT Wishlists.WishlistID, Wishlists.WishlistTitle, Wishlists.AuthorID, Wishlists.HeldOn, Products.ProductID, ProductTitle, ProductPrice, ProductManufacturer, ProductPathToImage, WishlistProducts.ReservedBy, WishlistProducts.Description
                FROM Wishlists\s
                JOIN WishlistGuests ON Wishlists.WishlistID = WishlistGuests.WishlistID
                LEFT JOIN WishlistProducts ON Wishlists.WishlistID = WishlistProducts.WishlistID\s
                LEFT JOIN Products ON WishlistProducts.ProductID = Products.ProductID
                WHERE WishlistGuests.UserID = ?;""", wishlistRowMapper, userID);
    }

    public int deleteWishlistByID(int wishlistID) {
        return jdbcTemplate.update("DELETE FROM Wishlists WHERE WishlistID = ?;", wishlistID);
    }

    public int deleteWishlistsByUser(int userID) {
        return jdbcTemplate.update("DELETE FROM Wishlists WHERE AuthorID = ?;", userID);
    }

    public List<Integer> getAllWishlistGuests(int wishlistID) {
        try {
            return jdbcTemplate.queryForList("SELECT UserID FROM WishlistGuests WHERE WishlistID = ?", Integer.class, wishlistID);
        }
        catch (EmptyResultDataAccessException erdae) {
            return null;
        }
    }
}
