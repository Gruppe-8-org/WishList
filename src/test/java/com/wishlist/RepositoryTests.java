package com.wishlist;

import com.wishlist.Model.Product;
import com.wishlist.Model.User;
import com.wishlist.Model.Wishlist;
import com.wishlist.Model.WishlistProduct;
import com.wishlist.Repository.ProductRepository;
import com.wishlist.Repository.UserRepository;
import com.wishlist.Repository.WishlistRepository;
import com.wishlist.RowMappers.UserRowMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = { "classpath:schemah2.sql", "classpath:datah2.sql"}, executionPhase = BEFORE_TEST_METHOD)

class RepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void getExistingUserGetsDesiredUser() {
        User u = userRepository.getUserByID(1); // Should be Markus
        assertThat(u).isNotNull();
        assertThat(u.getName()).isEqualTo("Markus");
        assertThat(u.getUsername()).isEqualTo("marqs69");
        assertThat(u.getPassword()).isEqualTo("12345678");
    }

    @Test
    public void getNonExistentUserReturnsNull() {
        assertThat(userRepository.getUserByID(4)).isNull();
    }

    @Test
    public void getExistingUserByUNameGetsDesiredUser() {
        User u = userRepository.getUserByUsername("lildawg");
        assertThat(u).isNotNull();
        assertThat(u.getName()).isEqualTo("Max-Emil");
        assertThat(u.getPassword()).isEqualTo("01101001");
    }

    @Test
    public void getNonExistentUserByUNameReturnsNull() {
        assertThat(userRepository.getUserByUsername("Jørgen")).isNull();
    }

    @Test
    public void addUserOnNewUserActuallyUpdates() {
        assertThat(userRepository.getUserByID(4)).isNull();

        User u = new User(4, "Jørgen", "jorgen1337", "01010101");
        int rowsAffected = userRepository.addUser(u);
        assertThat(rowsAffected).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("SELECT last_insert_id();", Integer.class)).isEqualTo(4);
        User jorgenFromDB = userRepository.getUserByID(4);
        assertThat(jorgenFromDB).isNotNull();
        assertThat(jorgenFromDB.getName()).isEqualTo("Jørgen");
        assertThat(jorgenFromDB.getUsername()).isEqualTo("jorgen1337");
        assertThat(jorgenFromDB.getPassword()).isEqualTo("01010101");
    }
    // No negative test here, as users assigned IDs dynamically in addUser.

    @Test
    public void updateExistingUserUpdatesDesiredFields() {
        assertThat(userRepository.getUserByID(1)).isNotNull();
        User editedUser = userRepository.getUserByID(1);
        editedUser.setName("Marqus");
        editedUser.setUsername("marqs");
        editedUser.setPassword("aGoodPassw0rd");
        int rowsAffected = userRepository.updateUser(editedUser);
        assertThat(rowsAffected).isEqualTo(1);

        User editedUserFromDB = userRepository.getUserByID(1);
        assertThat(editedUserFromDB.getName()).isEqualTo("Marqus");
        assertThat(editedUserFromDB.getUsername()).isEqualTo("marqs");
        assertThat(editedUserFromDB.getPassword()).isEqualTo("aGoodPassw0rd");
    }

    @Test
    public void updateNonExistentUserDoesNothing() {
        assertThat(userRepository.getUserByID(4)).isNull();
        User editedUser = new User(4, "Jørgen", "jorgen1337", "01010101");
        int rowsAffected = userRepository.updateUser(editedUser);
        assertThat(rowsAffected).isEqualTo(0);
    }

    @Test
    public void deleteUserDeletesExistingUser() {
        assertThat(userRepository.getUserByID(1)).isNotNull();
        List<User> allUsersBefore = jdbcTemplate.query("SELECT * FROM USERS;", new UserRowMapper());
        int rowsAffected = userRepository.deleteUserByID(1);
        assertThat(rowsAffected).isEqualTo(1);
        List<User> allUsersAfter = jdbcTemplate.query("SELECT * FROM USERS;", new UserRowMapper());
        assertThat(allUsersAfter.size()).isEqualTo(allUsersBefore.size() - 1);
        assertThat(userRepository.getUserByID(1)).isNull();
    }

    @Test
    public void deleteUserCascadesToWishlistGuests() {
        int usersFromDBWithID1Before = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistGuests WHERE UserID = 1;", Integer.class);
        assertThat(usersFromDBWithID1Before).isGreaterThan(0);
        int rowsAffected = userRepository.deleteUserByID(1);
        assertThat(rowsAffected).isEqualTo(1);
        int usersFromDBWithID1After = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistGuests WHERE UserID = 1;", Integer.class);
        assertThat(usersFromDBWithID1After).isEqualTo(0);
        assertThat(usersFromDBWithID1Before).isNotEqualTo(usersFromDBWithID1After);
    }

    @Test
    public void onlyWishlistAuthorOrGuestMayViewWishlists() {
        User newUser = new User(4, "Jørgen", "jorgen1337", "01010101");
        assertThat(userRepository.canViewWishlist(1, newUser.getID())).isFalse();
        assertThat(userRepository.canViewWishlist(2, newUser.getID())).isFalse();
        assertThat(userRepository.canViewWishlist(3, newUser.getID())).isFalse();
        assertThat(userRepository.canViewWishlist(1, 1)).isTrue(); // Markus is the author of this one
        assertThat(userRepository.canViewWishlist(2, 1)).isTrue(); // Shared with Markus by Max
        assertThat(userRepository.canViewWishlist(3, 1)).isTrue(); // Shared with Markus by Max-Emil
        jdbcTemplate.update("DELETE FROM WishlistGuests WHERE WishlistID = ? AND UserID = ?;", 2, 1); // Max stops sharing his wishlist with Markus
        assertThat(userRepository.canViewWishlist(2, 1)).isFalse(); // Markus can no longer see it
    }

    @Test
    public void reservingAWishSetsReservedToTrue() {
        boolean reserved = jdbcTemplate.queryForObject("SELECT COUNT(*) = 0 FROM WishlistProducts WHERE WishlistID = ? AND ProductID = ? AND ReservedBy IS NOT NULL", Boolean.class, 1, 1);
        assertThat(reserved).isTrue();
        int rowsAffected = userRepository.reserveWish(1, 1, 1);
        assertThat(rowsAffected).isEqualTo(1);
        reserved = jdbcTemplate.queryForObject("SELECT COUNT(*) = 0 FROM WishlistProducts WHERE WishlistID = ? AND ProductID = ? AND ReservedBy IS NOT NULL", Boolean.class, 1, 1);
        assertThat(reserved).isFalse();
        userRepository.reserveWish(1, 1, 1);
        reserved = jdbcTemplate.queryForObject("SELECT COUNT(*) = 0 FROM WishlistProducts WHERE WishlistID = ? AND ProductID = ? AND ReservedBy IS NOT NULL", Boolean.class, 1, 1);
        assertThat(reserved).isFalse(); // Doing it again shouldn't change the value
    }

    @Test
    public void unreservingAWishSetsReservedToFalse() {
        boolean reserved = jdbcTemplate.queryForObject("SELECT COUNT(*) = 0 FROM WishlistProducts WHERE WishlistID = ? AND ProductID = ? AND ReservedBy IS NOT NULL", Boolean.class, 1, 1);
        assertThat(reserved).isTrue();
        int rowsAffected = userRepository.reserveWish(1, 1, 1);
        assertThat(rowsAffected).isEqualTo(1);
        reserved = jdbcTemplate.queryForObject("SELECT COUNT(*) = 0 FROM WishlistProducts WHERE WishlistID = ? AND ProductID = ? AND ReservedBy IS NOT NULL", Boolean.class, 1, 1);
        assertThat(reserved).isFalse();
        rowsAffected = userRepository.unreserveWish(1, 1);
        assertThat(rowsAffected).isEqualTo(1);
        reserved = jdbcTemplate.queryForObject("SELECT COUNT(*) = 0 FROM WishlistProducts WHERE WishlistID = ? AND ProductID = ? AND ReservedBy IS NOT NULL", Boolean.class, 1, 1);
        assertThat(reserved).isTrue();
        userRepository.unreserveWish(1, 1);
        reserved = jdbcTemplate.queryForObject("SELECT COUNT(*) = 0 FROM WishlistProducts WHERE WishlistID = ? AND ProductID = ? AND ReservedBy IS NOT NULL", Boolean.class, 1, 1);
        assertThat(reserved).isTrue(); // Doing it again shouldn't change the value.
    }

    @Test
    public void getAllProductsWorksAsExpected() {
        List<String> names = List.of(
                "Lego Botanicals - Bonsai Tree",
                "Lego Architecture - Fallingwater",
                "Spotify Årskort",
                "Dyson Airwrap",
                "Faldskærmsudspringsgavekort",
                "Bugatti Veyron",
                "Rick James koncertbillet",
                "Mancera Cedrat Boise",
                "Versace Dylan Blue",
                "Dior Allure Homme Sport Eau Extréme"
        );

        List<Product> products = productRepository.getAllProducts();
        assertThat(products.size()).isEqualTo(10);

        for (int i = 0; i < products.size(); i++) {
            assertThat(names.get(i).equals(products.get(i).getTitle()));
        }
    }

    @Test
    public void getProductByIDWorksOnExistingProduct() {
        Product bonsai = productRepository.getProductByID(1);
        assertThat(bonsai.getTitle()).isEqualTo("Lego Botanicals - Bonsai Tree");
        assertThat(bonsai.getManufacturer()).isEqualTo("Lego");
        assertThat(bonsai.getPrice()).isEqualTo(449.95);
        assertThat(bonsai.getPathToImage()).isEqualTo("resources/static/lego_bonsai.png");
    }

    @Test
    public void getProductByIDReturnsNullOnNonExistentProduct() {
        Product banzai = productRepository.getProductByID(11);
        assertThat(banzai).isNull();
    }

    @Test
    public void addProductGrowsProductListByOneAndInsertsCorrectly() {
        int numProducts = productRepository.getAllProducts().size();
        Product newProduct = new Product(11, "DualShock 4", "Sony", "resources/static/dualshock4.png", 349.99);
        int rowsAffected = productRepository.addProduct(newProduct);
        int numProductsAfter = productRepository.getAllProducts().size();
        assertThat(numProductsAfter).isEqualTo(numProducts + 1);
        assertThat(rowsAffected).isEqualTo(1);
        Product newProductFromDB = productRepository.getProductByID(11);
        assertThat(newProductFromDB.getTitle().equals(newProduct.getTitle()));
        assertThat(newProductFromDB.getManufacturer().equals(newProduct.getManufacturer()));
        assertThat(newProductFromDB.getPathToImage().equals(newProduct.getPathToImage()));
        assertThat(newProductFromDB.getPrice()).isEqualTo(newProduct.getPrice());
    }

    @Test
    public void addProductsGrowsListByN() {
        int numProductsBefore = productRepository.getAllProducts().size();
        List<Product> products = List.of(
                new Product(11, "DualShock 4", "Sony", "resources/static/dualshock4.png", 349.99),
                new Product(12, "PlayStation 7", "Sony", "resources/static/ps7.png", 2749.99),
                new Product(13, "Kebabkongen Gavekort", "Kebabkongen", "resources/static/kebabkongen.png", 349.99),
                new Product(14, "Skate 4", "Electronic Arts", "resources/static/skate4.png", 349.99)
        );

        productRepository.addProducts(products);
        int numProductsAfter = productRepository.getAllProducts().size();
        assertThat(numProductsAfter).isEqualTo(numProductsBefore + 4);
    }
    
    @Test
    public void deleteProductShrinksListByOneAndDeletesCorrectly() {
        int numProductsBefore = productRepository.getAllProducts().size();
        int rowsAffected = productRepository.deleteProductByID(1);
        assertThat(rowsAffected).isEqualTo(1);
        int numProductsAfter = productRepository.getAllProducts().size();
        assertThat(numProductsAfter).isEqualTo(numProductsBefore - 1);
        assertThat(productRepository.getProductByID(1)).isNull();
    }

    @Test
    public void deleteProductCascadesToWishlistProducts() {
        int productsFromDBWithID1Before = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistProducts WHERE ProductID = 1;", Integer.class);
        assertThat(productsFromDBWithID1Before).isGreaterThan(0);
        int rowsAffected = productRepository.deleteProductByID(1);
        assertThat(rowsAffected).isEqualTo(1);
        int productsFromDBWithID1After = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistProducts WHERE ProductID = 1;", Integer.class);
        assertThat(productsFromDBWithID1After).isEqualTo(0);
        assertThat(productsFromDBWithID1Before).isNotEqualTo(productsFromDBWithID1After);
    }

    @Test
    public void updateProductSetsDesiredFieldsAndUpdatesOnlyOneProduct() {
        Product product1modifiedAllFields = productRepository.getProductByID(1);
        product1modifiedAllFields.setTitle("Lego Botanicals - Bonzai Tree");
        product1modifiedAllFields.setManufacturer("L3g0");
        product1modifiedAllFields.setPathToImage("resources/static/lego_bonzai.png");
        product1modifiedAllFields.setPrice(100.0);
        int rowsAffected = productRepository.updateProduct(product1modifiedAllFields);
        assertThat(rowsAffected).isEqualTo(1);
        Product product1modifiedFromDB = productRepository.getProductByID(1);
        assertThat(product1modifiedFromDB.getTitle()).isEqualTo("Lego Botanicals - Bonzai Tree");
        assertThat(product1modifiedFromDB.getManufacturer()).isEqualTo("L3g0");
        assertThat(product1modifiedFromDB.getPathToImage()).isEqualTo("resources/static/lego_bonzai.png");
        assertThat(product1modifiedFromDB.getPrice()).isEqualTo(100.0);


        Product product1modified2fields = productRepository.getProductByID(1);
        product1modified2fields.setTitle("L3g0 Botanicals - Bonzai Tree");
        product1modified2fields.setManufacturer("Leg0");
        productRepository.updateProduct(product1modified2fields);
        Product product1modified2fieldsFromDB = productRepository.getProductByID(1);
        assertThat(product1modified2fieldsFromDB.getTitle()).isEqualTo("L3g0 Botanicals - Bonzai Tree");
        assertThat(product1modified2fieldsFromDB.getManufacturer()).isEqualTo("Leg0");
        assertThat(product1modified2fieldsFromDB.getPathToImage()).isEqualTo("resources/static/lego_bonzai.png");
        assertThat(product1modified2fieldsFromDB.getPrice()).isEqualTo(100.0);
    }

    @Test
    public void addWishlistInsertsAWishlistAndAllItsProductsAndGuests() {
        List<Product> productsWishedFor = List.of(
                new Product(11, "DualShock 4", "Sony", "resources/static/dualshock4.png", 349.99),
                new Product(12, "PlayStation 7", "Sony", "resources/static/ps7.png", 2749.99),
                new Product(13, "Kebabkongen Gavekort", "Kebabkongen", "resources/static/kebabkongen.png", 349.99),
                new Product(14, "Skate 4", "Electronic Arts", "resources/static/skate4.png", 349.99)
        );

        for (Product product : productsWishedFor) {
            assertThat(productRepository.getProductByID(product.getID())).isNull();
        }

        Wishlist wishlist = wishlistRepository.getWishlistByID(4);
        assertThat(wishlist).isNull();

        int numGuestsForWishlistWithID4 = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistGuests WHERE WishlistID = 4;", Integer.class);
        assertThat(numGuestsForWishlistWithID4).isEqualTo(0);

        int numProductsForWishlistWithID4 = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistProducts WHERE WishlistID = 4;", Integer.class);
        assertThat(numProductsForWishlistWithID4).isEqualTo(0);

        List<WishlistProduct> wlProducts = productsWishedFor.stream().map(
                product -> new WishlistProduct(product, 0, "Link to product:")
        ).toList();

        Wishlist newWishlist = new Wishlist(4, "Max-Emils Julegaveønsker", 3, Date.from(Instant.now()), wlProducts);
        wishlistRepository.addWishlist(newWishlist, List.of(userRepository.getUserByID(1), userRepository.getUserByID(2)));

        for (Product product : productsWishedFor) {
            assertThat(productRepository.getProductByID(product.getID())).isNotNull();
        }

        wishlist = wishlistRepository.getWishlistByID(4);
        assertThat(wishlist).isNotNull();

        int numGuestsForWishlistWithID4after = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistGuests WHERE WishlistID = 4;", Integer.class);
        assertThat(numGuestsForWishlistWithID4after).isEqualTo(2);

        int numProductsForWishlistWithID4after = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistProducts WHERE WishlistID = 4;", Integer.class);
        assertThat(numProductsForWishlistWithID4after).isEqualTo(4);
    }

    @Test
    public void updateWishlistUpdatesDesiredFieldsProductsAndGuests() {
        List<Product> newWishes = List.of(
                new Product(11, "DualShock 4", "Sony", "resources/static/dualshock4.png", 349.99),
                new Product(12, "PlayStation 7", "Sony", "resources/static/ps7.png", 2749.99),
                new Product(13, "Kebabkongen Gavekort", "Kebabkongen", "resources/static/kebabkongen.png", 349.99),
                new Product(14, "Skate 4", "Electronic Arts", "resources/static/skate4.png", 349.99)
        );

        List<WishlistProduct> wlProducts = newWishes.stream().map(
                product -> new WishlistProduct(product, 0, "Link to product:")
        ).toList();

        Wishlist maxEmilsWishlistBefore = wishlistRepository.getWishlistByID(3);
        assertThat(maxEmilsWishlistBefore.getTitle()).isEqualTo("Fødselsdag");

        List<WishlistProduct> expectedProducts = List.of(
                new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"),
                new WishlistProduct(new Product(3, "Spotify Årskort", "Spotify", "resources/static/spotify_logo.png", 1500.0), 1, "Link to Product:"),
                new WishlistProduct(new Product(4, "Dyson Airwrap",  "Dyson", "resources/static/airwrap.png", 4100.0), 0, "Link to Product:"),
                new WishlistProduct(new Product(6, "Bugatti Veyron", "Bugatti", "resources/static/bugatti_veyron.png", 25000000.0), 0, "Link to Product:"),
                new WishlistProduct(new Product(9, "Versace Dylan Blue", "Versace", "resources/static/dylan_blue.png", 399.99), 0, "Link to Product:")
        );

        assertThat(maxEmilsWishlistBefore.getProducts().equals(expectedProducts));
        List<User> newGuests = List.of(userRepository.getUserByID(2));
        int numGuestsForWishlistWithID3 = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistGuests WHERE WishlistID = 3;", Integer.class);
        assertThat(numGuestsForWishlistWithID3).isEqualTo(2); // Markus & Max

        maxEmilsWishlistBefore.setProducts(wlProducts);
        maxEmilsWishlistBefore.setTitle("Max-Emil's julegaveønsker");
        wishlistRepository.updateWishlist(maxEmilsWishlistBefore, newGuests);
        Wishlist maxEmilsWishlistAfter = wishlistRepository.getWishlistByID(3);
        assertThat(maxEmilsWishlistAfter.getProducts().equals(wlProducts)); // Pretend the products existed previously through call to addProduct()
        assertThat(maxEmilsWishlistAfter.getTitle()).isEqualTo(maxEmilsWishlistBefore.getTitle());
        assertThat(maxEmilsWishlistAfter.getTitle()).isEqualTo("Max-Emil's julegaveønsker");

        int numGuestsForWishlistWithID3after = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistGuests WHERE WishlistID = 3;", Integer.class);
        assertThat(numGuestsForWishlistWithID3after).isEqualTo(1);

        int numProductsForWishlistWithID3after = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistProducts WHERE WishlistID = 3;", Integer.class);
        assertThat(numProductsForWishlistWithID3after).isEqualTo(4);
    }

    @Test
    public void getWishlistByIDWorksOnExistingWishlist() {
        Wishlist markusWishlist = wishlistRepository.getWishlistByID(1);
        List<WishlistProduct> expectedProducts = List.of(
                new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png", 449.95), 0, "Link to Product:"),
                new WishlistProduct(new Product(5, "Faldskærmsudspringsgavekort", "Dropzone Denmark", "resources/static/faldskærmsudspring.png", 2495.95), 0, "Link to Product:"),
                new WishlistProduct(new Product(6, "Bugatti Veyron", "Bugatti", "resources/static/bugatti_veyron.png",  25000000.0), 2, "Link to Product:"),
                new WishlistProduct(new Product(7, "Rick James koncertbillet", "TicketMaster", "resources/static/rick_james.png", 849.99), 0, "Link to Product:"),
                new WishlistProduct(new Product(10, "Dior Allure Homme Sport Eau Extréme", "Dior", "resources/static/AHSEE.png", 1299.99), 0, "Link to Product:")
        );

        assertThat(markusWishlist).isNotNull();
        assertThat(markusWishlist.getTitle()).isEqualTo("Fødselsdag");
        Calendar cal = Calendar.getInstance();
        cal.set(2025, Calendar.OCTOBER, 29, 0, 0, 0);
        java.util.Date calTime = cal.getTime();
        assertThat(markusWishlist.getHeldOn().equals(calTime));
        assertThat(markusWishlist.getAuthorID()).isEqualTo(1);
        assertThat(markusWishlist.getProducts().equals(expectedProducts));
    }

    @Test
    public void getWishlistByIDReturnsNullOnNonExistentWishlist() {
        assertThat(wishlistRepository.getWishlistByID(4)).isNull();
    }

    @Test
    public void getAllWishlistsByUserWithIDGetsAllWishlistsForUser() {
        assertThat(wishlistRepository.getAllWishlistsByUserWithID(1).size()).isEqualTo(1);
    }

    @Test
    public void getAllWishlistsSharedWithUserGetsAllSharedLists() {
        List<Wishlist> wishlistsSharedWithMarkus = wishlistRepository.getAllWishlistsUserIsInvitedTo(1);
        assertThat(wishlistsSharedWithMarkus.size()).isEqualTo(2);
        assertThat(wishlistsSharedWithMarkus.get(0).getTitle()).isEqualTo("Fødselsdag");
        assertThat(wishlistsSharedWithMarkus.get(0).getAuthorID()).isEqualTo(2);
        assertThat(wishlistsSharedWithMarkus.get(1).getTitle()).isEqualTo("Fødselsdag");
        assertThat(wishlistsSharedWithMarkus.get(1).getAuthorID()).isEqualTo(3);

        List<WishlistProduct> productsFromMaxWishlist = wishlistsSharedWithMarkus.get(0).getProducts();
        List<WishlistProduct> productsFromMaxEmilWishlist = wishlistsSharedWithMarkus.get(1).getProducts();

        List<WishlistProduct> expectedProductsMax = List.of(
                new WishlistProduct(new Product(2, "Lego Architecture - Fallingwater", "Lego", "resources/static/lego_fallingwater.png", 749.95), 0, "Link to Product:"),
                new WishlistProduct(new Product(5, "Faldskærmsudspringsgavekort", "Dropzone Denmark", "resources/static/faldskærmsudspring.png", 2495.95), 3, "Link to Product:"),
                new WishlistProduct(new Product(6, "Bugatti Veyron", "Bugatti", "resources/static/bugatti_veyron.png",  25000000.0), 0, "Link to Product:"),
                new WishlistProduct(new Product(7, "Rick James koncertbillet", "TicketMaster", "resources/static/rick_james.png", 849.99), 0, "Link to Product:"),
                new WishlistProduct(new Product(8, "Mancera Cedrat Boise", "Mancera", "resources/static/cedrat_boise.png", 1499.99), 0, "Link to Product:")
        );

        List<WishlistProduct> expectedProductsMaxEmil = List.of(
                new WishlistProduct(new Product(1, "Lego Botanicals - Bonsai Tree", "Lego", "resources/static/lego_bonsai.png",  449.95), 0, "Link to Product:"),
                new WishlistProduct(new Product(3, "Spotify Årskort", "Spotify", "resources/static/spotify_logo.png", 1500.0), 1, "Link to Product:"),
                new WishlistProduct(new Product(4, "Dyson Airwrap",  "Dyson", "resources/static/airwrap.png", 4100.0), 0, "Link to Product:"),
                new WishlistProduct(new Product(6, "Bugatti Veyron", "Bugatti", "resources/static/bugatti_veyron.png", 25000000.0), 0, "Link to Product:"),
                new WishlistProduct(new Product(9, "Versace Dylan Blue", "Versace", "resources/static/dylan_blue.png", 399.99), 0, "Link to Product:")
        );

        assertThat(productsFromMaxWishlist.equals(expectedProductsMax));
        assertThat(productsFromMaxEmilWishlist.equals(expectedProductsMaxEmil));
    }

    @Test
    public void deleteWishlistByIDDeletesExistingWishlist() {
        int numWishlistsBefore = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Wishlists;", Integer.class);
        assertThat(numWishlistsBefore).isEqualTo(3);
        int rowsAffected = wishlistRepository.deleteWishlistByID(1);
        assertThat(rowsAffected).isEqualTo(1);
        int numWishlistsAfter = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Wishlists;", Integer.class);
        assertThat(numWishlistsAfter).isEqualTo(2);
    }

    @Test
    public void deleteWishlistByIDDoesNothingOnNonExistentWishlist() {
        int numWishlistsBefore = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Wishlists;", Integer.class);
        assertThat(numWishlistsBefore).isEqualTo(3);
        int rowsAffected = wishlistRepository.deleteWishlistByID(4);
        assertThat(rowsAffected).isEqualTo(0);
    }

    @Test
    public void deleteWishlistByIDCascadesToWishlistGuestsAndProducts() {
        int numWishlistProductsWithWishlistID1before = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistProducts WHERE WishlistID = 1;", Integer.class);
        int numWishlistGuestsForWishlistID1before = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistGuests WHERE WishlistID = 1;", Integer.class);
        assertThat(numWishlistProductsWithWishlistID1before).isEqualTo(5);
        assertThat(numWishlistGuestsForWishlistID1before).isEqualTo(2);
        int rowsAffected = wishlistRepository.deleteWishlistByID(1);
        assertThat(rowsAffected).isEqualTo(1);
        int numWishlistProductsWithWishlistID1after = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistProducts WHERE WishlistID = 1;", Integer.class);
        int numWishlistGuestsForWishlistID1after = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistGuests WHERE WishlistID = 1;", Integer.class);
        assertThat(numWishlistProductsWithWishlistID1after).isEqualTo(0);
        assertThat(numWishlistGuestsForWishlistID1after).isEqualTo(0);
    }

    @Test
    public void deleteWishlistsByUserDeletesWishlistByUserWhenExists() {
        int rowsAffected = wishlistRepository.deleteWishlistsByUser(1);
        assertThat(rowsAffected).isEqualTo(1);
        assertThat(wishlistRepository.getWishlistByID(1)).isNull();
        rowsAffected = wishlistRepository.deleteWishlistsByUser(4);
        assertThat(rowsAffected).isEqualTo(0);
    }

    @Test
    public void deleteWishlistsByUserDeletesWishlistsByUserWhenExists() {
        List<Product> productsWishedFor = List.of(
                new Product(11, "DualShock 4", "Sony", "resources/static/dualshock4.png", 349.99),
                new Product(12, "PlayStation 7", "Sony", "resources/static/ps7.png", 2749.99),
                new Product(13, "Kebabkongen Gavekort", "Kebabkongen", "resources/static/kebabkongen.png", 349.99),
                new Product(14, "Skate 4", "Electronic Arts", "resources/static/skate4.png", 349.99)
        );

        List<WishlistProduct> wlProducts = productsWishedFor.stream().map(
                product -> new WishlistProduct(product, 0, "Link to product:")
        ).toList();

        wishlistRepository.addWishlist(new Wishlist(4, "Markus' julegaveønsker", 1, Date.from(Instant.now()), wlProducts), List.of(userRepository.getUserByID(2), userRepository.getUserByID(3)));
        int rowsAffected = wishlistRepository.deleteWishlistsByUser(1);
        assertThat(rowsAffected).isEqualTo(2);
    }

    @Test
    public void deleteWishlistsByUserCascadesToWishlistGuestsAndProducts() {
        int numWishlistProductsWithWishlistID1before = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistProducts WHERE WishlistID = 1;", Integer.class);
        int numWishlistGuestsForWishlistID1before = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistGuests WHERE WishlistID = 1;", Integer.class);
        assertThat(numWishlistProductsWithWishlistID1before).isEqualTo(5);
        assertThat(numWishlistGuestsForWishlistID1before).isEqualTo(2);
        int rowsAffected = wishlistRepository.deleteWishlistsByUser(1);
        assertThat(rowsAffected).isEqualTo(1);
        int numWishlistProductsWithWishlistID1after = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistProducts WHERE WishlistID = 1;", Integer.class);
        int numWishlistGuestsForWishlistID1after = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM WishlistGuests WHERE WishlistID = 1;", Integer.class);
        assertThat(numWishlistProductsWithWishlistID1after).isEqualTo(0);
        assertThat(numWishlistGuestsForWishlistID1after).isEqualTo(0);
    }
}
