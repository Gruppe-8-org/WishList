package com.wishlist;

import com.wishlist.Exceptions.EntityAlreadyExistsException;
import com.wishlist.Exceptions.EntityDoesNotExistException;
import com.wishlist.Exceptions.OperationNotAllowedException;
import com.wishlist.Exceptions.ZeroRowsAffectedOnUpdateException;
import com.wishlist.Model.Product;
import com.wishlist.Model.User;
import com.wishlist.Model.Wishlist;
import com.wishlist.Model.WishlistProduct;
import com.wishlist.Repository.ProductRepository;
import com.wishlist.Repository.UserRepository;
import com.wishlist.Repository.WishlistRepository;
import com.wishlist.Service.UserService;
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
public class UserServiceTests {
    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserService userService;

    @Test
    public void getUserByIDReturnsExistingUser() {
        User expectedUser = new User(1, "Markus", "marqs69", "12345678");
        when(userRepository.getUserByID(1)).thenReturn(expectedUser);
        User actualUser = userService.getUserByID(1);
        assertEquals(expectedUser, actualUser);
        verify(userRepository).getUserByID(1);
    }

    @Test
    public void getUserByIDThrowsOnNonExistentUser() {
        when(userRepository.getUserByID(10)).thenReturn(null);
        assertThrows(EntityDoesNotExistException.class,
                () -> userService.getUserByID(10),
                "User with ID 10 does not exist.");
        verify(userRepository).getUserByID(10);
    }

    @Test
    public void getAllUsersGetsAllExistingUsers() {
        List<User> expectedUsers = List.of(
                new User(1, "Markus", "marqs69", "12345678"),
                new User(2, "Max", "bigdawg", "87654321"),
                new User(3, "Max-Emil", "lildawg", "01101001")
        );

        when(userRepository.getAllUsers()).thenReturn(expectedUsers);
        List<User> actualUsers = userService.getAllUsers();
        assertEquals(actualUsers, expectedUsers);
        verify(userRepository).getAllUsers();
    }

    @Test
    public void getAllUsersAlsoWorksOnEmptyUserList() {
        when(userRepository.getAllUsers()).thenReturn(List.of());
        assertEquals(List.of(), userService.getAllUsers());
        verify(userRepository).getAllUsers();
    }

    @Test
    public void addUserAddsAUser() {
        User newUser = new User(4, "Jørgen", "jorgmeister", "13243546");
        when(userRepository.addUser(newUser)).thenReturn(1);
        assertEquals(1, userService.addUser(newUser));
        verify(userRepository).addUser(newUser);
    }

    @Test
    public void addUserThrowsOnAddingExistingUser() {
        User newUser = new User(4, "Jørgen", "jorgmeister", "13243546");
        when(userRepository.addUser(newUser)).thenReturn(1);
        assertEquals(1, userService.addUser(newUser));
        when(userRepository.addUser(newUser)).thenThrow(new EntityAlreadyExistsException("User with username jorgmeister already exists."));
        assertThrows(EntityAlreadyExistsException.class,
                () -> userService.addUser(newUser),
                "User with username jorgmeister already exists.");
        verify(userRepository, times(2)).addUser(newUser);
    }

    @Test
    public void updateUserWorksOnExistingUser() {
        User userToTarget = new User(1, "Markus", "marqs69", "12345678");
        when(userRepository.getUserByID(1)).thenReturn(userToTarget);
        User userChanged = new User(1, "Markus", "marqs", "safepassword");
        when(userRepository.updateUser(userChanged)).thenReturn(1);
        assertEquals(1, userService.updateUser(userChanged));
        verify(userRepository).getUserByID(1);
        verify(userRepository).updateUser(userChanged);
    }

    @Test
    public void updateUserThrowsOnNonExistentUser() {
        User userChanged = new User(1, "Markus", "marqs", "safepassword");
        when(userRepository.getUserByID(1)).thenReturn(null);

        assertThrows(EntityDoesNotExistException.class,
                () -> userService.updateUser(userChanged),
                "User with ID 1 does not exist.");

        verify(userRepository).getUserByID(1);
        verify(userRepository, never()).updateUser(userChanged);
    }

    @Test
    public void updateUserThrowsWhenZeroRowsUpdated() {
        User userOriginal = new User(1, "Markus", "marqs", "safepassword");
        User userChanged = new User(1, "Markus", "marqs", "safepassword");
        when(userRepository.getUserByID(1)).thenReturn(userOriginal);
        when(userRepository.updateUser(userChanged)).thenReturn(0);

        assertThrows(ZeroRowsAffectedOnUpdateException.class,
                () -> userService.updateUser(userChanged),
                "Zero rows updated, but user with ID 1 exists");

        verify(userRepository).getUserByID(1);
        verify(userRepository).updateUser(userChanged);
    }

    @Test
    public void deleteUserByIDDeletesExistingUser() {
        User user = new User(1, "Markus", "marqs", "safepassword");
        when(userRepository.getUserByID(1)).thenReturn(user);
        when(userRepository.deleteUserByID(1)).thenReturn(1);
        assertEquals(1, userService.deleteUserByID(1));
        verify(userRepository).getUserByID(1);
        verify(userRepository).deleteUserByID(1);
    }

    @Test
    public void deleteUserByIDThrowsOnNonExistentUser() {
        when(userRepository.getUserByID(1)).thenReturn(null);

        assertThrows(EntityDoesNotExistException.class,
                () -> userService.deleteUserByID(1),
                "User with ID 1 does not exist.");

        verify(userRepository).getUserByID(1);
        verify(userRepository, never()).deleteUserByID(1);
    }

    @Test
    public void deleteUserByIDThrowsOnZeroRowsUpdated() {
        User userOriginal = new User(1, "Markus", "marqs", "safepassword");
        when(userRepository.getUserByID(1)).thenReturn(userOriginal);
        when(userRepository.deleteUserByID(1)).thenReturn(0);

        assertThrows(ZeroRowsAffectedOnUpdateException.class,
                () -> userService.deleteUserByID(1),
                "Zero rows deleted, but user with ID 1 exists");

        verify(userRepository).getUserByID(1);
        verify(userRepository).deleteUserByID(1);
    }

    @Test
    public void canViewWishlistWorksOnExistingWishlistAndUser() {
        User userOriginal = new User(1, "Markus", "marqs", "safepassword");
        List<WishlistProduct> products = List.of(new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"));
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), products);
        when(userRepository.getUserByID(1)).thenReturn(userOriginal);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(wishlist);
        when(wishlistRepository.getWishlistByID(2)).thenReturn(wishlist);
        when(userRepository.canViewWishlist(1, 1)).thenReturn(true);
        when(userRepository.canViewWishlist(2, 1)).thenReturn(false);
        assertTrue(userService.userCanViewWishList(1, 1));
        assertFalse(userService.userCanViewWishList(2, 1));
        verify(userRepository, times(2)).getUserByID(1);
        verify(wishlistRepository).getWishlistByID(1);
        verify(wishlistRepository).getWishlistByID(2);
        verify(userRepository).canViewWishlist(1, 1);
        verify(userRepository).canViewWishlist(2, 1);
    }

    @Test
    public void canViewWishlistThrowsOnNonExistentUser() {
        List<WishlistProduct> products = List.of(new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"));
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), products);
        when(userRepository.getUserByID(1)).thenReturn(null);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(wishlist);

        assertThrows(EntityDoesNotExistException.class,
                () -> userService.userCanViewWishList(1, 1),
                "User with ID 1 does not exist.");

        verify(userRepository).getUserByID(1);
        verify(wishlistRepository).getWishlistByID(1);
        verify(userRepository, never()).canViewWishlist(1, 1);
    }

    @Test
    public void canViewWishlistThrowsOnNonExistentWishlist() {
        User userOriginal = new User(1, "Markus", "marqs", "safepassword");
        when(userRepository.getUserByID(1)).thenReturn(userOriginal);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(null);

        assertThrows(EntityDoesNotExistException.class,
                () -> userService.userCanViewWishList(1, 1),
                "Wishlist with ID 1 does not exist.");

        verify(wishlistRepository).getWishlistByID(1);
        verify(userRepository, never()).canViewWishlist(1, 1);
        verify(userRepository, never()).getUserByID(1);
    }

    @Test
    public void getReserverIDWorksOnExistingWishlistAndProduct() {
        Product product = new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95);
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), List.of(new WishlistProduct(product, 0, "")));
        when(productRepository.getProductByID(1)).thenReturn(product);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(wishlist);
        when(userRepository.getReserverID(1, 1)).thenReturn(1);
        assertEquals(1, userService.getReserverID(1, 1));
        verify(productRepository).getProductByID(1);
        verify(wishlistRepository).getWishlistByID(1);
        verify(userRepository).getReserverID(1, 1);
    }

    @Test
    public void getReserverIDFailsOnNonExistentWishlist() {
        when(wishlistRepository.getWishlistByID(1)).thenReturn(null);

        assertThrows(EntityDoesNotExistException.class,
                () -> userService.getReserverID(1, 1),
                "Wishlist with ID 1 does not exist.");

        verify(productRepository, never()).getProductByID(1);
        verify(wishlistRepository).getWishlistByID(1);
        verify(userRepository, never()).getReserverID(1, 1);
    }

    @Test
    public void getReserverIDFailsOnNonExistentProduct() {
        Product product = new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95);
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), List.of(new WishlistProduct(product, 0, "")));
        when(productRepository.getProductByID(1)).thenReturn(null);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(wishlist);

        assertThrows(EntityDoesNotExistException.class,
                () -> userService.getReserverID(1, 1),
                "Product with ID 1 does not exist.");

        verify(productRepository).getProductByID(1);
        verify(wishlistRepository).getWishlistByID(1);
        verify(userRepository, never()).getReserverID(1, 1);
    }

    @Test
    public void reserveWishWorksOnProductNotReserved() {
        Product product = new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95);
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), List.of(new WishlistProduct(product, 0, "")));
        when(productRepository.getProductByID(1)).thenReturn(product);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(wishlist);
        when(userRepository.getReserverID(1, 1)).thenReturn(0);
        when(userRepository.reserveWish(1, 1, 1)).thenReturn(1);
        assertEquals(1, userService.userReserveWish(1, 1, 1));
        verify(productRepository, times(3)).getProductByID(1);
        verify(wishlistRepository, times(3)).getWishlistByID(1);
        verify(userRepository, times(2)).getReserverID(1, 1);
        verify(userRepository).reserveWish(1, 1, 1);
    }

    @Test
    public void reserveThrowsOnProductReservedByAnotherUser() {
        Product product = new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95);
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), List.of(new WishlistProduct(product, 0, "")));
        when(productRepository.getProductByID(1)).thenReturn(product);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(wishlist);
        when(userRepository.getReserverID(1, 1)).thenReturn(2);

        assertThrows(OperationNotAllowedException.class,
                () -> userService.userReserveWish(1, 1, 1),
                "You cannot reserve something already reserved by another user.");

        verify(productRepository, times(3)).getProductByID(1);
        verify(wishlistRepository, times(3)).getWishlistByID(1);
        verify(userRepository, times(2)).getReserverID(1, 1);
        verify(userRepository, never()).reserveWish(1, 1, 1);
    }

    @Test
    public void reserveThrowsOnNonExistentWishlist() {
        when(wishlistRepository.getWishlistByID(1)).thenReturn(null);

        assertThrows(EntityDoesNotExistException.class,
                () -> userService.userReserveWish(1, 1, 1),
                "Wishlist with ID 1 does not exist.");

        verify(productRepository, never()).getProductByID(1);
        verify(wishlistRepository).getWishlistByID(1);
        verify(userRepository, never()).getReserverID(1, 1);
        verify(userRepository, never()).reserveWish(1, 1, 1);
    }

    @Test
    public void reserveThrowsOnNonExistentProduct() {
        Product product = new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95);
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), List.of(new WishlistProduct(product, 0, "")));
        when(productRepository.getProductByID(1)).thenReturn(null);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(wishlist);

        assertThrows(EntityDoesNotExistException.class,
                () -> userService.userReserveWish(1, 1, 1),
                "Product with ID 1 does not exist.");

        verify(productRepository).getProductByID(1);
        verify(wishlistRepository).getWishlistByID(1);
        verify(userRepository, never()).getReserverID(1, 1);
        verify(userRepository, never()).reserveWish(1, 1, 1);
    }

    @Test
    public void reserveThrowsIfUpdateFailed() {
        Product product = new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95);
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), List.of(new WishlistProduct(product, 0, "")));
        when(productRepository.getProductByID(1)).thenReturn(product);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(wishlist);
        when(userRepository.getReserverID(1, 1)).thenReturn(0);
        when(userRepository.reserveWish(1, 1, 1)).thenReturn(0);

        assertThrows(ZeroRowsAffectedOnUpdateException.class,
                () -> userService.userReserveWish(1, 1, 1),
                "Couldn't reserve wish with WishlistID 1, productID 1, but such a WishlistProduct exists");

        verify(productRepository, times(3)).getProductByID(1);
        verify(wishlistRepository, times(3)).getWishlistByID(1);
        verify(userRepository, times(2)).getReserverID(1, 1);
        verify(userRepository).reserveWish(1, 1, 1);
    }

    @Test
    public void unreserveWishWorksOnReservedProduct() {
        Product product = new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95);
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), List.of(new WishlistProduct(product, 1, "")));
        when(productRepository.getProductByID(1)).thenReturn(product);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(wishlist);
        when(userRepository.getReserverID(1, 1)).thenReturn(1);
        when(userRepository.unreserveWish(1, 1)).thenReturn(1);
        assertEquals(1, userService.userUnreserveWish(1, 1, 1));
        verify(productRepository, times(3)).getProductByID(1);
        verify(wishlistRepository, times(3)).getWishlistByID(1);
        verify(userRepository, times(2)).getReserverID(1, 1);
        verify(userRepository).unreserveWish(1, 1);
    }

    @Test
    public void unreserveWishThrowsOnProductReservedByAnother() {
        Product product = new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95);
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), List.of(new WishlistProduct(product, 2, "")));
        when(productRepository.getProductByID(1)).thenReturn(product);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(wishlist);
        when(userRepository.getReserverID(1, 1)).thenReturn(2);
        when(userRepository.unreserveWish(1, 1)).thenReturn(1);


        assertThrows(OperationNotAllowedException.class,
                () -> userService.userUnreserveWish(1, 1, 1),
                "You cannot unreserve something reserved by another user.");


        verify(productRepository, times(3)).getProductByID(1);
        verify(wishlistRepository, times(3)).getWishlistByID(1);
        verify(userRepository, times(2)).getReserverID(1, 1);
        verify(userRepository, never()).unreserveWish(1, 1);
    }

    @Test
    public void unreserveWishThrowsOnProductNotReserved() {
        Product product = new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95);
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), List.of(new WishlistProduct(product, 0, "")));
        when(productRepository.getProductByID(1)).thenReturn(product);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(wishlist);
        when(userRepository.getReserverID(1, 1)).thenReturn(0);
        when(userRepository.unreserveWish(1, 1)).thenReturn(1);


        assertThrows(OperationNotAllowedException.class,
                () -> userService.userUnreserveWish(1, 1, 1),
                "You cannot unreserve something not reserved.");


        verify(productRepository, times(2)).getProductByID(1);
        verify(wishlistRepository, times(2)).getWishlistByID(1);
        verify(userRepository).getReserverID(1, 1);
        verify(userRepository, never()).unreserveWish(1, 1);
    }

    @Test
    public void unreserveWishThrowsOnNonExistentProduct() {
        Product product = new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95);
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), List.of(new WishlistProduct(product, 0, "")));
        when(productRepository.getProductByID(1)).thenReturn(null);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(wishlist);

        assertThrows(EntityDoesNotExistException.class,
                () -> userService.userUnreserveWish(1, 1, 1),
                "Product with ID 1 does not exist.");


        verify(productRepository).getProductByID(1);
        verify(wishlistRepository).getWishlistByID(1);
        verify(userRepository, never()).getReserverID(1, 1);
        verify(userRepository, never()).unreserveWish(1, 1);
    }

    @Test
    public void unreserveWishThrowsOnNonExistentWishlist() {
        when(wishlistRepository.getWishlistByID(1)).thenReturn(null);

        assertThrows(EntityDoesNotExistException.class,
                () -> userService.userUnreserveWish(1, 1, 1),
                "Wishlist with ID 1 does not exist.");


        verify(productRepository, never()).getProductByID(1);
        verify(wishlistRepository).getWishlistByID(1);
        verify(userRepository, never()).getReserverID(1, 1);
        verify(userRepository, never()).unreserveWish(1, 1);
    }

    @Test
    public void unreserveWishThrowsOnZeroRowsUpdated() {
        Product product = new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95);
        Wishlist wishlist = new Wishlist(1, "Fødselsdag", 1, LocalDate.now(), List.of(new WishlistProduct(product, 1, "")));
        when(productRepository.getProductByID(1)).thenReturn(product);
        when(wishlistRepository.getWishlistByID(1)).thenReturn(wishlist);
        when(userRepository.getReserverID(1, 1)).thenReturn(1);
        when(userRepository.unreserveWish(1, 1)).thenReturn(0);

        assertThrows(ZeroRowsAffectedOnUpdateException.class,
                () -> userService.userUnreserveWish(1, 1, 1),
                "Couldn't reserve wish with WishlistID 1, productID 1, but such a WishlistProduct exists");

        verify(productRepository, times(3)).getProductByID(1);
        verify(wishlistRepository, times(3)).getWishlistByID(1);
        verify(userRepository, times(2)).getReserverID(1, 1);
        verify(userRepository).unreserveWish(1, 1);
    }

    @Test
    public void loginReturnsTrueOnExistingUserOnMatchingCredentials() {
        User userOriginal = new User(1, "Markus", "marqs", "safepassword");
        when(userRepository.getUserByUsername("marqs")).thenReturn(userOriginal);
        assertTrue(userService.login(userOriginal.getUsername(), userOriginal.getPassword()));
        verify(userRepository).getUserByUsername("marqs");
    }

    @Test
    public void loginReturnsFalseOnExistingUserWithWrongCredentials() {
        User userOriginal = new User(1, "Markus", "marqs", "safepassword");
        when(userRepository.getUserByUsername("marqs")).thenReturn(userOriginal);
        assertFalse(userService.login(userOriginal.getUsername(), "abc"));
        verify(userRepository).getUserByUsername("marqs");
    }

    @Test
    public void loginReturnsFalseOnNonExistentUser() {
        when(userRepository.getUserByUsername("max")).thenReturn(null);
        assertFalse(userService.login("max", "abc"));
        verify(userRepository).getUserByUsername("max");
    }

    @Test
    public void getUserByUsernameReturnsUserForExistingUser() {
        User userOriginal = new User(1, "Markus", "marqs", "safepassword");
        when(userRepository.getUserByUsername("marqs")).thenReturn(userOriginal);
        assertEquals(userOriginal, userService.getUserByUsername("marqs"));
        verify(userRepository).getUserByUsername("marqs");
    }

    @Test
    public void getUserByUsernameThrowsOnNonExistentUser() {
        when(userRepository.getUserByUsername("marqs")).thenReturn(null);

        assertThrows(EntityDoesNotExistException.class,
                () -> userService.getUserByUsername("marqs"),
                "User with Username marqs does not exist.");

        verify(userRepository).getUserByUsername("marqs");
    }
}
