package com.wishlist.Repository;

import com.wishlist.Model.User;
import com.wishlist.RowMappers.UserRowMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    @Value("${spring.datasource.url}")
    private String dbURL;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    protected final JdbcTemplate jdbcTemplate;
    protected final UserRowMapper userRowMapper;

    public UserRepository(JdbcTemplate jdbcTemplate, UserRowMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    public User getUserByID(int userID) {
        User userToReturn;

        try {
            userToReturn = jdbcTemplate.queryForObject("SELECT * FROM Users WHERE UserID = ?;", userRowMapper, userID);
        } catch (EmptyResultDataAccessException erdae) {
            userToReturn = null;
        }

        return userToReturn;
    }

    public int addUser(User user) {
        return jdbcTemplate.update("INSERT IGNORE INTO Users (User_Name, Username, Password) VALUES (?, ?, ?);",
                user.getName(), user.getUsername(), user.getPassword());
    }

    public int updateUser(User editedUser) {
        return jdbcTemplate.update("UPDATE Users SET User_Name = ?, Username = ?, Password = ? WHERE UserID = ?;",
                editedUser.getName(), editedUser.getUsername(), editedUser.getPassword(), editedUser.getID());
    }

    public int deleteUserByID(int userID) {
        return jdbcTemplate.update("DELETE FROM Users WHERE UserID = ?;", userID);
    }

    /*
    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }

    Should be in our Controller. Set to private there instead of public.
     */

    public boolean canViewWishlist(int wishlistID, int userID) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject("SELECT COUNT(*) > 0 FROM WishlistGuests WHERE WishlistID = ? AND UserID = ?;",
                boolean.class, wishlistID, userID)) || // User is a set as a guest of the wishlist.
                Boolean.TRUE.equals(jdbcTemplate.queryForObject("SELECT COUNT(*) > 0 FROM Wishlists WHERE WishlistID = ? AND Wishlists.AuthorID = ?;",
                        Boolean.class, wishlistID, userID)); // User is the author of the wishlist.
    }

    public void reserveWish(int wishlistID, int productID) {
        jdbcTemplate.update("UPDATE WishlistProducts SET Reserved = TRUE WHERE WishlistID = ? AND ProductID = ?;",
                wishlistID, productID);
        // Don't care about rows affected since product may already be reserved.
        // Should never be greater than 1 however.
    }

    public void unreserveWish(int wishlistID, int productID) {
        jdbcTemplate.update("UPDATE WishlistProducts SET Reserved = FALSE WHERE WishlistID = ? AND ProductID = ?;",
                wishlistID, productID);
    }
}
