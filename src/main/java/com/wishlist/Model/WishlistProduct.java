package com.wishlist.Model;

public class WishlistProduct {
    private Product product;
    private boolean isReserved;

    public WishlistProduct(Product product, boolean isReserved) {
        this.product = product;
        this.isReserved = isReserved;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }
}
