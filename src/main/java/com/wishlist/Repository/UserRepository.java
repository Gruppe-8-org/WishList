package com.wishlist.Repository;

import com.wishlist.Model.User;
import com.wishlist.Model.UserRowMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

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

    public int addUser(User user) {
        return jdbcTemplate.update("INSERT IGNORE INTO Users (Name, Username, Password) VALUES (?, ?, ?);",
                user.getName(), user.getUsername(), user.getPassword());
    }

    // Todo: Select which one we'll use when templates are done.
    public int deleteUser(User user) {
        return jdbcTemplate.update("DELETE FROM Users WHERE ID = ?;", user.getID());
    }

    public int deleteUserByID(int userID) {
        return jdbcTemplate.update("DELETE FROM Users WHERE ID = ?;", userID);
    }

    public boolean isLoggedIn(HttpSession session) {
        return !session.getAttribute("username").toString().isEmpty();
    }

    public boolean canViewWishlist(int wishlistID, int userID) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject("SELECT COUNT(*) > 0 FROM WishlistGuests WHERE WishlistID = ? AND UserID = ?;",
                boolean.class, wishlistID, userID));
    }

    public int reserveWish(int wishlistID, int productID) {
        return jdbcTemplate.update("UPDATE WishlistProducts SET Reserved = TRUE WHERE WishlistID = ? AND ProductID = ?;",
                wishlistID, productID);
    }
}
