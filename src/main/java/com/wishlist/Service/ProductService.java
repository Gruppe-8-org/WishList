package com.wishlist.Service;

import com.wishlist.Model.Product;
import com.wishlist.Repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    public ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProductByID(int wishId) {
        return productRepository.getProductByID(wishId);
    }

    public List<Product> getProductsByWishlistID(int wishlistID) {
        List<Product> products = productRepository.getProductsByWishlistID(wishlistID);
        return products != null ? products : List.of();
    }

}
