package com.wishlist.Model;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component // Annotation creates bean for DI in Repository.
public class WishlistRowMapper implements RowMapper<Wishlist> {
    private final WishlistProductRowMapper wishlistProductRowMapper;

    public WishlistRowMapper(WishlistProductRowMapper wishlistProductRowMapper) {
        this.wishlistProductRowMapper = wishlistProductRowMapper;
    }

    @Override
    public Wishlist mapRow(ResultSet rs, int rowNum) throws SQLException {
        List<WishlistProduct> products = new ArrayList<WishlistProduct>();

        do {
            WishlistProduct wishlistProduct = wishlistProductRowMapper.mapRow(rs, rowNum);

            if (wishlistProduct.getProduct() != null) {
                products.add(wishlistProduct);
            }

            rowNum++;
        } while (rs.next());

        return new Wishlist(rs.getInt("ID"), rs.getString("Title"), rs.getInt("AuthorID"), rs.getDate("HeldOn"), products);
    }
}
