package com.wishlist.Model;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component // Annotation creates bean for DI in Repository.
public class WishlistProductRowMapper implements RowMapper<WishlistProduct> {
    private final ProductRowMapper productRowMapper;

    public WishlistProductRowMapper(ProductRowMapper productRowMapper) {
        this.productRowMapper = productRowMapper;
    }

    @Override
    public WishlistProduct mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = productRowMapper.mapRow(rs, rowNum);
        return new WishlistProduct(product, rs.getBoolean("Reserved"));
    }
}
