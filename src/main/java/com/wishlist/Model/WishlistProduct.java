package com.wishlist.Model;

import java.util.Objects;

public class WishlistProduct {
    private Product product;
    private int reserverID;
    private String description;

    public WishlistProduct(Product product, int reserverID, String description) {
        this.product = product;
        this.reserverID = reserverID;
        this.description = description;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean isReserved() {
        return reserverID != 0;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReserverID() {
        return reserverID;
    }

    public void setReserverID(int reserverID) {
        this.reserverID = reserverID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WishlistProduct that = (WishlistProduct) o;
        return reserverID == that.reserverID && Objects.equals(product, that.product) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, reserverID, description);
    }
}
