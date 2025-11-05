package com.wishlist;

import com.wishlist.Exceptions.EntityDoesNotExistException;
import com.wishlist.Exceptions.ZeroRowsAffectedOnUpdateException;
import com.wishlist.Model.Product;
import com.wishlist.Model.User;
import com.wishlist.Model.Wishlist;
import com.wishlist.Model.WishlistProduct;
import com.wishlist.Repository.ProductRepository;
import com.wishlist.Repository.UserRepository;
import com.wishlist.Repository.WishlistRepository;
import com.wishlist.Service.WishlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
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
public class WishlistServiceTests {
    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private WishlistRepository wishlistRepository;

    @Autowired
    private WishlistService wishlistService;

    @Test
    public void getWishlistByIDReturnsExistingWishlist() {
        List<WishlistProduct> products = List.of(new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"));
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), products);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(wishlist);
        assertEquals(wishlist, wishlistService.getWishlistByID(1));
        verify(wishlistRepository).getWishlistByID(1);
    }

    @Test
    public void getWishlistByIDThrowsOnNonExistentWishlist() {
        when(wishlistRepository.getWishlistByID(1)).thenReturn(null);
        assertThrows(EntityDoesNotExistException.class,
                () -> wishlistService.getWishlistByID(1),
                "Wishlist with ID 1 does not exist.");
        verify(wishlistRepository).getWishlistByID(1);
    }

    @Test
    public void getAllWishlistsByUserWithIDWorksOnExistingUser() {
        List<WishlistProduct> products = List.of(new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"));
        List<Wishlist> wishlists = List.of(new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), products));
        when(wishlistRepository.getAllWishlistsByUserWithID(1)).thenReturn(wishlists);
        when(userRepository.getUserByID(1)).thenReturn(new User(1, "Markus", "marqs69", "12345678"));
        assertEquals(wishlists, wishlistService.getAllWishlistsByUserWithID(1));
        verify(wishlistRepository).getAllWishlistsByUserWithID(1);
        verify(userRepository).getUserByID(1);
    }

    @Test
    public void getAllWishlistsByUserWithIDThrowsOnNonExistentUser() {
        List<WishlistProduct> products = List.of(new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"));
        List<Wishlist> wishlists = List.of(new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), products));
        when(wishlistRepository.getAllWishlistsByUserWithID(1)).thenReturn(wishlists);
        when(userRepository.getUserByID(1)).thenReturn(null);

        assertThrows(EntityDoesNotExistException.class,
                () -> wishlistService.getAllWishlistsByUserWithID(1),
                "User with ID 1 does not exist");

        verify(wishlistRepository, never()).getAllWishlistsByUserWithID(1);
        verify(userRepository).getUserByID(1);
    }

    @Test
    public void getAllWishlistsUserIsInvitedToWorksOnExistingUserOnGuestsList() {
        List<WishlistProduct> products = List.of(new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"));
        List<Wishlist> wishlists = List.of(new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), products));
        when(wishlistRepository.getAllWishlistsUserIsInvitedTo(1)).thenReturn(wishlists);
        when(userRepository.getUserByID(1)).thenReturn(new User(1, "Markus", "marqs69", "12345678"));
        assertEquals(wishlists, wishlistService.getAllWishlistsUserIsInvitedTo(1));
        verify(wishlistRepository).getAllWishlistsUserIsInvitedTo(1);
        verify(userRepository).getUserByID(1);
    }

    @Test
    public void getAllWishlistsUserIsInvitedToWorksOnExistingUserNotOnGuestsList() {
        List<WishlistProduct> products = List.of(new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"));
        List<Wishlist> wishlists = List.of();
        when(wishlistRepository.getAllWishlistsUserIsInvitedTo(1)).thenReturn(wishlists);
        when(userRepository.getUserByID(1)).thenReturn(new User(1, "Markus", "marqs69", "12345678"));
        assertEquals(wishlists, wishlistService.getAllWishlistsUserIsInvitedTo(1));
        verify(wishlistRepository).getAllWishlistsUserIsInvitedTo(1);
        verify(userRepository).getUserByID(1);
    }

    @Test
    public void getAllWishlistsUserIsInvitedToThrowsOnNonExistentUser() {
        List<WishlistProduct> products = List.of(new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"));
        List<Wishlist> wishlists = List.of(new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), products));
        when(wishlistRepository.getAllWishlistsUserIsInvitedTo(1)).thenReturn(wishlists);
        when(userRepository.getUserByID(1)).thenReturn(null);

        assertThrows(EntityDoesNotExistException.class,
                () -> wishlistService.getAllWishlistsUserIsInvitedTo(1),
                "User with ID 1 does not exist");

        verify(wishlistRepository, never()).getAllWishlistsUserIsInvitedTo(1);
        verify(userRepository).getUserByID(1);
    }

    @Test
    public void deleteWishlistByIDWorksOnExistingWishlist() {
        List<WishlistProduct> products = List.of(new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"));
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), products);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(wishlist);
        when(wishlistRepository.deleteWishlistByID(1)).thenReturn(1);
        assertEquals(1, wishlistService.deleteWishlistByID(1));
        verify(wishlistRepository).getWishlistByID(1);
        verify(wishlistRepository).deleteWishlistByID(1);
    }

    @Test
    public void deleteWishlistByIDThrowsOnNonExistentWishlist() {
        when(wishlistRepository.getWishlistByID(1)).thenReturn(null);
        when(wishlistRepository.deleteWishlistByID(1)).thenReturn(1);
        assertThrows(EntityDoesNotExistException.class,
                () -> wishlistService.deleteWishlistByID(1),
                "Wishlist with ID 1 does not exist");
        verify(wishlistRepository).getWishlistByID(1);
        verify(wishlistRepository, never()).deleteWishlistByID(1);
    }

    @Test
    public void deleteWishlistByIDThrowsOnZeroRowsAffected() {
        List<WishlistProduct> products = List.of(new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"));
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), products);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(wishlist);
        when(wishlistRepository.deleteWishlistByID(1)).thenReturn(0);
        assertThrows(ZeroRowsAffectedOnUpdateException.class,
                () -> wishlistService.deleteWishlistByID(1),
                "Zero rows deleted, but wishlist with ID 1 exists.");
        verify(wishlistRepository).getWishlistByID(1);
        verify(wishlistRepository).deleteWishlistByID(1);
    }

    @Test
    public void deleteAllWishlistsByUserWorksOnExistingUser() {
        List<WishlistProduct> products = List.of(new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"));
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), products);
        when(userRepository.getUserByID(1)).thenReturn(new User(1, "Markus", "marqs69", "12345678"));
        doNothing().when(wishlistRepository).addWishlist(wishlist, List.of());
        when(wishlistRepository.deleteWishlistsByUser(1)).thenReturn(1);
        assertEquals(1, wishlistService.deleteAllWishlistsByUser(1));
        verify(userRepository).getUserByID(1);
        verify(wishlistRepository).deleteWishlistsByUser(1);
    }

    @Test
    public void deleteAllWishlistsByUserThrowsOnNonExistentUser() {
        List<WishlistProduct> products = List.of(new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"));
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), products);
        when(userRepository.getUserByID(1)).thenReturn(null);
        doNothing().when(wishlistRepository).addWishlist(wishlist, List.of());
        when(wishlistRepository.deleteWishlistsByUser(1)).thenReturn(1);

        assertThrows(EntityDoesNotExistException.class,
                () -> wishlistService.deleteAllWishlistsByUser(1),
                "User with ID 1 does not exist");

        verify(userRepository).getUserByID(1);
        verify(wishlistRepository, never()).deleteWishlistsByUser(1);
    }

    @Test
    public void deleteAllWishlistsByUserThrowsOnZeroRowsAffected() {
        List<WishlistProduct> products = List.of(new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"));
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), products);
        when(userRepository.getUserByID(1)).thenReturn(new User(1, "Markus", "marqs69", "12345678"));
        doNothing().when(wishlistRepository).addWishlist(wishlist, List.of());
        when(wishlistRepository.deleteWishlistsByUser(1)).thenReturn(0);

        assertThrows(ZeroRowsAffectedOnUpdateException.class,
                () -> wishlistService.deleteAllWishlistsByUser(1),
                "Zero rows deleted, but user with ID 1 exists.");

        verify(userRepository).getUserByID(1);
        verify(wishlistRepository).deleteWishlistsByUser(1);
    }

    @Test
    public void getAllWishlistGuestsReturnsAllGuestsIfWishlistHasThem() {
        List<WishlistProduct> products = List.of(new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"));
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), products);
        List<Integer> guestIndices = List.of(2, 3);
        when(wishlistRepository.getAllWishlistGuests(1)).thenReturn(guestIndices);
        assertEquals(guestIndices, wishlistService.getAllWishlistGuests(1));
        verify(wishlistRepository).getAllWishlistGuests(1);
    }

    @Test
    public void getAllWishlistGuestsReturnsAllGuestsIfWishlistDoesNotHaveThem() {
        List<WishlistProduct> products = List.of(new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"));
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), products);
        List<Integer> guestIndices = List.of();
        when(wishlistRepository.getAllWishlistGuests(1)).thenReturn(guestIndices);
        assertEquals(guestIndices, wishlistService.getAllWishlistGuests(1));
        verify(wishlistRepository).getAllWishlistGuests(1);
    }
}
