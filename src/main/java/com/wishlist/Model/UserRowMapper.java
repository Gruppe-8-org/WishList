package com.wishlist.Model;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component // Annotation creates bean for DI in Repository.
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User (
                rs.getInt("ID"),
                rs.getString("Name"),
                rs.getString("Username"),
                rs.getString("Password")
        );
    }
}

