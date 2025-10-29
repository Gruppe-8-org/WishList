package com.wishlist.Model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        WishlistProduct that = (WishlistProduct) o;
        return isReserved == that.isReserved && Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, isReserved);
    }
}
