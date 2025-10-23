package com.wishlist.RowMappers;

import com.wishlist.Model.Wishlist;
import com.wishlist.Model.WishlistProduct;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component // Annotation creates bean for DI in Repository.
public class WishlistRowMapper implements RowMapper<Wishlist> {
    private final WishlistProductRowMapper wishlistProductRowMapper;

    public WishlistRowMapper(WishlistProductRowMapper wishlistProductRowMapper) {
        this.wishlistProductRowMapper = wishlistProductRowMapper;
    }

    @Override
    public Wishlist mapRow(ResultSet rs, int rowNum) throws SQLException {
        List<WishlistProduct> products = new ArrayList<>();
        int wishlistID = rs.getInt("WishlistID");
        String wishlistTitle = rs.getString("WishlistTitle");
        int wishlistAuthorID = rs.getInt("AuthorID");
        Date wishlistHeldOn = rs.getDate("HeldOn");

        do {
            WishlistProduct wishlistProduct = wishlistProductRowMapper.mapRow(rs, rowNum);

            if (wishlistProduct != null) {
                products.add(wishlistProduct);
            }

            rowNum++;
        } while (rs.next() && rs.getInt("WishlistID") == wishlistID);

        return new Wishlist(wishlistID, wishlistTitle, wishlistAuthorID, wishlistHeldOn, products);
    }
}
