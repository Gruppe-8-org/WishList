package com.wishlist.Model;

import java.time.LocalDate;
import java.util.List;

public class WishlistDTO {
    private String wishlistTitle;
    private LocalDate wishlistHeldOn;
    private List<Integer> productIDs;
    private List<String> productDescriptions;
    private List<Integer> guestIDs;

    public List<Integer> getProductIDs() {
        return productIDs;
    }

    public void setProductIDs(List<Integer> productIDs) {
        this.productIDs = productIDs;
    }

    public List<String> getProductDescriptions() {
        return productDescriptions;
    }

    public void setProductDescriptions(List<String> productDescriptions) {
        this.productDescriptions = productDescriptions;
    }

    public List<Integer> getGuestIDs() {
        return guestIDs;
    }

    public void setGuestIDs(List<Integer> guestIDs) {
        this.guestIDs = guestIDs;
    }

    public LocalDate getWishlistHeldOn() {
        return wishlistHeldOn;
    }

    public String getWishlistTitle() {
        return wishlistTitle;
    }

    public void setWishlistTitle(String wishlistTitle) {
        this.wishlistTitle = wishlistTitle;
    }

    public void setWishlistHeldOn(LocalDate localDate) {
        this.wishlistHeldOn = localDate;
    }
}
