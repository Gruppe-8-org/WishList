package com.wishlist.Model;

import java.util.Objects;

public class Product {
    private int ID;
    private String title;
    private String manufacturer;
    private String pathToImage;
    private double price;

    public Product() {

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return ID == product.ID && Double.compare(price, product.price) == 0 && Objects.equals(title, product.title) && Objects.equals(manufacturer, product.manufacturer) && Objects.equals(pathToImage, product.pathToImage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, title, manufacturer, pathToImage, price);
    }

    public Product(int ID, String title, String manufacturer, String pathToImage, double price) {
        this.ID = ID;
        this.title = title;
        this.manufacturer = manufacturer;
        this.pathToImage = pathToImage;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getPathToImage() {
        return pathToImage;
    }

    public void setPathToImage(String pathToImage) {
        this.pathToImage = pathToImage;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
