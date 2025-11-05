package com.wishlist;

import com.wishlist.Exceptions.EntityDoesNotExistException;
import com.wishlist.Exceptions.ZeroRowsAffectedOnUpdateException;
import com.wishlist.Model.Product;
import com.wishlist.Model.WishlistProduct;
import com.wishlist.Repository.ProductRepository;
import com.wishlist.Repository.UserRepository;
import com.wishlist.Repository.WishlistRepository;
import com.wishlist.Service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
public class ProductServiceTests {
    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private WishlistRepository wishlistRepository;

    @Autowired
    private ProductService productService;

    @Test
    public void getAllProductsReturnsAllProductsOnExistingProductList() {
        List<Product> expectedProducts = List.of(
                new Product(11, "DualShock 4", "Sony", "resources/static/dualshock4.png", 349.99),
                new Product(12, "PlayStation 7", "Sony", "resources/static/ps7.png", 2749.99),
                new Product(13, "Kebabkongen Gavekort", "Kebabkongen", "resources/static/kebabkongen.png", 349.99),
                new Product(14, "Skate 4", "Electronic Arts", "resources/static/skate4.png", 349.99)
        );

        when(productRepository.getAllProducts()).thenReturn(expectedProducts);
        List<Product> actualProducts = productService.getAllProducts();
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository).getAllProducts();
    }

    @Test
    public void getAllProductsReturnsEmptyListOnNoProducts() {
        List<Product> expectedProducts = List.of();
        when(productRepository.getAllProducts()).thenReturn(expectedProducts);
        List<Product> actualProducts = productService.getAllProducts();
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository).getAllProducts();
    }

    @Test
    public void getAllWishlistProductsWorksOnExistingProductList() {
        List<WishlistProduct> expectedWishlistProducts = List.of(
                new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png", 449.95), 0, "Link to Product:"),
                new WishlistProduct(new Product(5, "Faldskærmsudspringsgavekort", "Dropzone Denmark", "resources/static/faldskærmsudspring.png", 2495.95), 0, "Link to Product:"),
                new WishlistProduct(new Product(6, "Bugatti Veyron", "Bugatti", "resources/static/bugatti_veyron.png",  25000000.0), 2, "Link to Product:"),
                new WishlistProduct(new Product(7, "Rick James koncertbillet", "TicketMaster", "resources/static/rick_james.png", 849.99), 0, "Link to Product:"),
                new WishlistProduct(new Product(10, "Dior Allure Homme Sport Eau Extréme", "Dior", "resources/static/AHSEE.png", 1299.99), 0, "Link to Product:"));

        when(productRepository.getAllWishlistProducts()).thenReturn(expectedWishlistProducts);
        List<WishlistProduct> actualProducts = productService.getAllWishlistProducts();
        assertEquals(expectedWishlistProducts, actualProducts);
        verify(productRepository).getAllWishlistProducts();
    }

    @Test
    public void getAllWishlistProductsReturnsEmptyListOnNoProducts() {
        List<WishlistProduct> expectedProducts = List.of();
        when(productRepository.getAllWishlistProducts()).thenReturn(expectedProducts);
        List<WishlistProduct> actualProducts = productService.getAllWishlistProducts();
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository).getAllWishlistProducts();
    }

    @Test
    public void getProductByIDReturnsProductWhenExists() {
        Product product = new Product(11, "DualShock 4", "Sony", "resources/static/dualshock4.png", 349.99);
        when(productRepository.getProductByID(11)).thenReturn(product);
        assertEquals(product, productService.getProductByID(11));
        verify(productRepository).getProductByID(11);
    }

    @Test
    public void getProductByIDThrowsOnNonExistentProduct() {
        when(productRepository.getProductByID(1)).thenReturn(null);
        assertThrows(EntityDoesNotExistException.class,
                () -> productService.getProductByID(1),
                "Product with ID 1 does not exist.");
        verify(productRepository).getProductByID(1);
    }

    @Test
    public void updateProductWorksOnExistingProduct() {
        Product product = new Product(11, "DualShock 4", "Sony", "resources/static/dualshock4.png", 349.99);
        Product updatedProduct =  new Product(11, "DualShock 4", "Zony", "resources/static/dualshock4.png", 349.99);
        when(productRepository.getProductByID(11)).thenReturn(product);
        when(productRepository.updateProduct(updatedProduct)).thenReturn(1);
        assertEquals(1, productService.updateProduct(updatedProduct));
        verify(productRepository).getProductByID(11);
        verify(productRepository).updateProduct(updatedProduct);
    }

    @Test
    public void updateProductWorksOnNonExistentProduct() {
        Product updatedProduct =  new Product(11, "DualShock 4", "Zony", "resources/static/dualshock4.png", 349.99);
        when(productRepository.getProductByID(11)).thenReturn(null);
        when(productRepository.updateProduct(updatedProduct)).thenReturn(1);

        assertThrows(EntityDoesNotExistException.class,
                () -> productService.updateProduct(updatedProduct),
                "Product with ID 11 does not exist");

        verify(productRepository).getProductByID(11);
        verify(productRepository, never()).updateProduct(updatedProduct);
    }

    @Test
    public void updateProductThrowsOnZeroRowsAffected() {
        Product product = new Product(11, "DualShock 4", "Sony", "resources/static/dualshock4.png", 349.99);
        Product updatedProduct =  new Product(11, "DualShock 4", "Zony", "resources/static/dualshock4.png", 349.99);
        when(productRepository.getProductByID(11)).thenReturn(product);
        when(productRepository.updateProduct(updatedProduct)).thenReturn(0);

        assertThrows(ZeroRowsAffectedOnUpdateException.class,
                () -> productService.updateProduct(updatedProduct),
                "Zero rows updated, but product with ID 11 exists");

        verify(productRepository).getProductByID(11);
        verify(productRepository).updateProduct(updatedProduct);
    }

    @Test
    public void addProductUpdatesListOfProducts() {
        Product product = new Product(11, "DualShock 4", "Sony", "resources/static/dualshock4.png", 349.99);
        when(productRepository.addProduct(product)).thenReturn(1);
        when(productRepository.getProductByID(11)).thenReturn(product);
        assertEquals(1, productService.addProduct(product));
        assertNotNull(productService.getProductByID(11));
        verify(productRepository).addProduct(product);
        verify(productRepository).getProductByID(11);
    }

    @Test
    public void deleteProductByIDWorksOnExistingProduct() {
        Product product = new Product(11, "DualShock 4", "Sony", "resources/static/dualshock4.png", 349.99);
        when(productRepository.getProductByID(11)).thenReturn(product);
        when(productRepository.deleteProductByID(11)).thenReturn(1);
        assertEquals(1, productService.deleteProductByID(11));
        verify(productRepository).getProductByID(11);
        verify(productRepository).deleteProductByID(11);
    }

    @Test
    public void deleteProductThrowsOnNonExistentProduct() {
        when(productRepository.getProductByID(11)).thenReturn(null);

        assertThrows(EntityDoesNotExistException.class,
                () -> productService.deleteProductByID(11),
                "Product with ID 11 does not exist.");

        verify(productRepository).getProductByID(11);
        verify(productRepository, never()).deleteProductByID(11);
    }

    @Test
    public void deleteProductThrowsOnZeroRowsAffected() {
        Product product = new Product(11, "DualShock 4", "Sony", "resources/static/dualshock4.png", 349.99);
        when(productRepository.getProductByID(11)).thenReturn(product);
        when(productRepository.deleteProductByID(11)).thenReturn(0);

        assertThrows(ZeroRowsAffectedOnUpdateException.class,
                () -> productService.deleteProductByID(11),
                "Zero rows deleted, but product with ID 11 exists");

        verify(productRepository).getProductByID(11);
        verify(productRepository).deleteProductByID(11);
    }
}
