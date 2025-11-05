package com.wishlist.Repository;

import com.wishlist.Model.Product;
import com.wishlist.Model.WishlistProduct;
import com.wishlist.RowMappers.ProductRowMapper;
import com.wishlist.RowMappers.WishlistProductRowMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepository {
    @Value("${spring.datasource.url}")
    private String dbURL;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    protected final JdbcTemplate jdbcTemplate;
    protected final ProductRowMapper productRowMapper;
    protected final WishlistProductRowMapper wishlistProductRowMapper;

    public ProductRepository(JdbcTemplate jdbcTemplate, ProductRowMapper productRowMapper, WishlistProductRowMapper wishlistProductRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.productRowMapper = productRowMapper;
        this.wishlistProductRowMapper = wishlistProductRowMapper;
    }

    public List<Product> getAllProducts() {
        return jdbcTemplate.query("SELECT * FROM Products;", productRowMapper);
    }

    public List<WishlistProduct> getAllWishlistProducts() {
        return jdbcTemplate.query("SELECT * FROM Products LEFT JOIN WishlistProducts ON Products.ProductID = WishlistProducts.ProductID ;", wishlistProductRowMapper);
    }

    public int updateProduct(Product product) {
        return jdbcTemplate.update("UPDATE Products SET ProductTitle = ?, ProductPrice = ?, ProductManufacturer = ?, ProductPathToImage = ? WHERE ProductID = ?;",
                product.getTitle(), product.getPrice(), product.getManufacturer(), product.getPathToImage(), product.getID());
    }

    public Product getProductByID(int productID) {
        Product productToReturn;

        try {
            productToReturn = jdbcTemplate.queryForObject("SELECT * FROM Products WHERE ProductID = ?;", productRowMapper, productID);
        } catch (EmptyResultDataAccessException erdae) {
            productToReturn = null;
        }

        return productToReturn;
    }

    public int addProduct(Product product) {
        return jdbcTemplate.update("INSERT IGNORE INTO Products (ProductID, ProductTitle, ProductManufacturer, ProductPathToImage, ProductPrice) VALUES (?, ?, ?, ?, ?);",
                product.getID(), product.getTitle(), product.getManufacturer(), product.getPathToImage(), product.getPrice());
    }

    public void addProducts(List<Product> products) {
        for (Product p : products) {
            addProduct(p); // Discards return value
        }
    }

    public int deleteProductByID(int productID) {
        return jdbcTemplate.update("DELETE FROM Products WHERE ProductID = ?;", productID);
    }
}
