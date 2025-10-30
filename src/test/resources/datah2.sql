USE WishList;

INSERT INTO Users (UserID, User_Name, Username, Password) VALUES
                                                              (1, 'Markus', 'marqs69', '12345678'), (2, 'Max', 'bigdawg', '87654321'), (3, 'Max-Emil', 'lildawg', '01101001');

INSERT INTO Wishlists (WishlistID, AuthorID, WishlistTitle, HeldOn) VALUES
                                                                        (1, 1, 'Fødselsdag', '20251029'), (2, 2, 'Fødselsdag', '20251029'), (3, 3, 'Fødselsdag', '20251029');

INSERT INTO Products (ProductID, ProductTitle, ProductPrice, ProductManufacturer, ProductPathToImage) VALUES
                                                                                                          (1, 'Lego Botanicals - Bonsai Tree', 449.95, 'Lego', 'resources/static/lego_bonsai.png'),
                                                                                                          (2, 'Lego Architecture - Fallingwater', 749.95, 'Lego', 'resources/static/lego_fallingwater.png'),
                                                                                                          (3, 'Spotify Årskort', 1500.0, 'Spotify', 'resources/static/spotify_logo.png'),
                                                                                                          (4, 'Dyson Airwrap', 4100.0, 'Dyson', 'resources/static/airwrap.png'),
                                                                                                          (5, 'Faldskærmsudspringsgavekort', 2495.95, 'Dropzone Denmark', 'resources/static/faldskærmsudspring.png'),
                                                                                                          (6, 'Bugatti Veyron', 25000000.0, 'Bugatti', 'resources/static/bugatti_veyron.png'),
                                                                                                          (7, 'Rick James koncertbillet', 849.99, 'TicketMaster', 'resources/static/rick_james.png'),
                                                                                                          (8, 'Mancera Cedrat Boise', 1499.99, 'Mancera', 'resources/static/cedrat_boise.png'),
                                                                                                          (9, 'Versace Dylan Blue', 399.99, 'Versace', 'resources/static/dylan_blue.png'),
                                                                                                          (10, 'Dior Allure Homme Sport Eau Extréme', 1299.99, 'Dior', 'resources/static/AHSEE.png');

INSERT INTO WishlistProducts (WishlistID, ProductID, Description, ReservedBy) VALUES
                                                                      (1, 1, 'Link to product:', null), (1, 5, 'Link to product:', null), (1, 6, 'Link to product:', 2), (1, 7, 'Link to product:', null), (1, 10, 'Link to product:', null),
                                                                      (2, 2, 'Link to product:', null), (2, 5, 'Link to product:', 3), (2, 6, 'Link to product:', null), (2, 7, 'Link to product:', null), (2, 8, 'Link to product:', null),
                                                                      (3, 1, 'Link to product:', null), (3, 3, 'Link to product:', 1), (3, 4, 'Link to product:', null), (3, 6, 'Link to product:', null), (3, 9, 'Link to product:', null);


INSERT INTO WishlistGuests (WishlistID, UserID) VALUES
                                                    (1, 2), (1, 3),
                                                    (2, 1), (2, 3),
                                                    (3, 1), (3, 2);

-- Intet for sessions