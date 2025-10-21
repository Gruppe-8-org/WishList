package com.wishlist.Model;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component // Annotation creates bean for DI in Repository.
public class ProductRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Product(
                rs.getInt("ID"),
                rs.getString("Title"),
                rs.getString("Manufacturer"),
                rs.getString("PathToImage"),
                rs.getDouble("Price")
        );
    }
}
