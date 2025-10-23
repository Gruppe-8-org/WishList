package com.wishlist.RowMappers;

import com.wishlist.Model.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component // Annotation creates bean for DI in Repository.
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User (
                rs.getInt("UserID"),
                rs.getString("User_Name"),
                rs.getString("Username"),
                rs.getString("Password")
        );
    }
}

