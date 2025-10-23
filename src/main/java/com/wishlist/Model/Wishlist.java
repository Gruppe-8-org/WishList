package com.wishlist.Model;

import java.util.Date;
import java.util.List;

public class Wishlist {
    private int ID;
    private String title;
    private int authorID;
    private Date heldOn;
    private List<WishlistProduct> products;

    public Wishlist() {
    }

    public Wishlist(int ID, String title, int authorID, Date heldOn, List<WishlistProduct> products) {
        this.ID = ID;
        this.title = title;
        this.authorID = authorID;
        this.heldOn = heldOn;
        this.products = products;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getHeldOn() {
        return heldOn;
    }

    public void setHeldOn(Date heldOn) {
        this.heldOn = heldOn;
    }

    public List<WishlistProduct> getProducts() {
        return products;
    }

    public void setProducts(List<WishlistProduct> products) {
        this.products = products;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getAuthorID() {
        return authorID;
    }

    public void setAuthor(int authorID) {
        this.authorID = authorID;
    }
}
