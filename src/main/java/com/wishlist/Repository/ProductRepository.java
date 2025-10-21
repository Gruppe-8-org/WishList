package com.wishlist.Repository;

import com.wishlist.Model.Product;
import com.wishlist.Model.ProductRowMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

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
        return jdbcTemplate.queryForList("SELECT * FROM Products", Product.class);
    }

    public Product getProductByID(int productID) {
        return jdbcTemplate.queryForObject("SELECT * FROM Products WHERE ID = ?", Product.class, productID);
    }

    public int addProduct(Product product) {
        return jdbcTemplate.update("INSERT IGNORE INTO Products (ID, Title, Manufacturer, PathToImage, Price) VALUES (?, ?, ?, ?, ?)",
                product.getID(), product.getTitle(), product.getManufacturer(), product.getPathToImage(), product.getPrice());
    }

    public void addProducts(List<Product> products) {
        for (Product p : products) {
            addProduct(p); // Discards return value
        }
    }

    public int deleteProductByID(int productID) {
        return jdbcTemplate.update("DELETE FROM Products WHERE ID = ?", productID);
    }
}
