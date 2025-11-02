package com.wishlist.Service;

import com.wishlist.Exceptions.EntityDoesNotExistException;
import com.wishlist.Exceptions.ZeroRowsAffectedOnUpdateException;
import com.wishlist.Model.Product;
import com.wishlist.Repository.ProductRepository;

import java.util.List;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }

    public Product getProductByID(int productID) {
        Product product = productRepository.getProductByID(productID);

        if (product == null) {
            throw new EntityDoesNotExistException("Product with ID " + productID + " does not exist.");
        }

        return product;
    }

    public void updateProduct(Product product) {
        if (productRepository.getProductByID(product.getID()) == null) {
            throw new EntityDoesNotExistException("Product with ID " + product.getID() + " does not exist.");
        }

        int rowsAffected = productRepository.updateProduct(product);
        if (rowsAffected < 1) {
            throw new ZeroRowsAffectedOnUpdateException("Zero rows updated, but product with ID " + product.getID() + " exists");
        }
    }

    public void addProduct(Product product) {
        productRepository.addProduct(product);
    }

    public void addProducts(List<Product> products) {
        productRepository.addProducts(products);
    }

    public void deleteProductByID(int productID) {
        if (productRepository.getProductByID(productID) == null) {
            throw new EntityDoesNotExistException("Product with ID " + productID + " does not exist.");
        }

        int rowsAffected = productRepository.deleteProductByID(productID);
        if (rowsAffected < 1) {
            throw new ZeroRowsAffectedOnUpdateException("Zero rows deleted, but product with ID " + productID + " exists");
        }
    }
}
