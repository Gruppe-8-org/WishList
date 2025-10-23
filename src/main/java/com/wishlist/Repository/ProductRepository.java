package com.wishlist.Repository;

import com.wishlist.Model.Product;
import com.wishlist.RowMappers.ProductRowMapper;
import org.springframework.beans.factory.annotation.Value;
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

    public ProductRepository(JdbcTemplate jdbcTemplate, ProductRowMapper productRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.productRowMapper = productRowMapper;
    }

    public List<Product> getAllProducts() {
        return jdbcTemplate.query("SELECT * FROM Products", productRowMapper);
    }

    public Product getProductByID(int productID) {
        return jdbcTemplate.queryForObject("SELECT * FROM Products WHERE ProductID = ?", productRowMapper, productID);
    }

    public int addProduct(Product product) {
        return jdbcTemplate.update("INSERT IGNORE INTO Products (ProductID, ProductTitle, ProductManufacturer, ProductPathToImage, ProductPrice) VALUES (?, ?, ?, ?, ?)",
                product.getID(), product.getTitle(), product.getManufacturer(), product.getPathToImage(), product.getPrice());
    }

//udkommenteret og tilføjet en anden lige nedenunder for at se om det får programmet til at virke i forhold til at tilføje en wishlist
//    public List<Product> getProductsByWishlistID(int wishlistID) {
//        String sql = "SELECT * FROM Products WHERE WishlistID = ?";
//        return jdbcTemplate.query(sql, productRowMapper, wishlistID);
//    }

    public List<Product> getProductsByWishlistID(int wishlistID) {
        String sql = """
        SELECT p.ProductID, p.ProductTitle, p.ProductManufacturer, p.ProductPathToImage, p.ProductPrice
        FROM Products p
        JOIN WishlistProducts wp ON p.ProductID = wp.ProductID
        WHERE wp.WishlistID = ?
        """;
        return jdbcTemplate.query(sql, productRowMapper, wishlistID);
    }

    public void addProducts(List<Product> products) {
        for (Product p : products) {
            addProduct(p); // Discards return value
        }
    }

    public int deleteProductByID(int productID) {
        return jdbcTemplate.update("DELETE FROM Products WHERE ProductID = ?", productID);
    }
}
