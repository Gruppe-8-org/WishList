DROP SCHEMA IF EXISTS WishList;
CREATE SCHEMA IF NOT EXISTS WishList;
USE WishList;

DROP TABLE IF EXISTS Products;
DROP TABLE IF EXISTS WishlistProducts;
DROP TABLE IF EXISTS WishlistUsers;
DROP TABLE IF EXISTS Reservations;
DROP TABLE IF EXISTS Wishlists;
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Sessions;


CREATE TABLE IF NOT EXISTS Users (
                                     UserID INT PRIMARY KEY AUTO_INCREMENT,
                                     User_Name VARCHAR(64),
    Username VARCHAR(64) UNIQUE,
    Password TEXT
    );

CREATE TABLE IF NOT EXISTS Wishlists (
                                         WishlistID INT PRIMARY KEY AUTO_INCREMENT,
                                         AuthorID INT,
                                         WishlistTitle TEXT,
                                         HeldOn DATETIME,
                                         FOREIGN KEY (AuthorID) REFERENCES Users(UserID) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS Products (
                                        ProductID INT PRIMARY KEY AUTO_INCREMENT,
                                        ProductTitle TEXT,
                                        ProductPrice DOUBLE,
                                        ProductManufacturer TEXT,
                                        ProductPathToImage VARCHAR(512)
    );

CREATE TABLE IF NOT EXISTS WishlistProducts (
                                                WishlistID INT,
                                                ProductID INT,
                                                ReservedBy INT NULL,
                                                Description TEXT,
                                                PRIMARY KEY (WishlistID, ProductID),
    FOREIGN KEY (WishlistID) REFERENCES Wishlists(WishlistID) ON DELETE CASCADE,
    FOREIGN KEY (ProductID) REFERENCES Products(ProductID) ON DELETE CASCADE,
    FOREIGN KEY (ReservedBy) REFERENCES Users(UserID) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS WishlistGuests (
                                              WishlistID INT,
                                              UserID INT,
                                              PRIMARY KEY (WishlistID, UserID),
    FOREIGN KEY (WishlistID) REFERENCES Wishlists(WishlistID) ON DELETE CASCADE,
    FOREIGN KEY (UserID) REFERENCES Users(UserID) ON DELETE CASCADE
    );
