package com.wishlist.Model;

import org.springframework.jdbc.core.RowMapper;

import java.util.Date;
import java.util.List;

public class Wishlist {
    private String title;
    private Date heldOn;
    private List<Product> products;

    public Wishlist(String title, Date heldOn, List<Product> products) {
        this.title = title;
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

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
