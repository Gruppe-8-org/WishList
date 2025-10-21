package com.wishlist.Model;

import org.springframework.jdbc.core.RowMapper;

public class Product {
    private String title;
    private String manufacturer;
    private String pathToImage;
    private double price;

    public Product(String title, String manufacturer, String pathToImage, double price) {
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
}
