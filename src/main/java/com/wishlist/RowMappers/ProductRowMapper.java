package com.wishlist.RowMappers;

import com.wishlist.Model.Product;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component // Annotation creates bean for DI in Repository.
public class ProductRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Product(
                rs.getInt("ProductID"),
                rs.getString("ProductTitle"),
                rs.getString("ProductManufacturer"),
                rs.getString("ProductPathToImage"),
                rs.getDouble("ProductPrice")
        );
    }
}
