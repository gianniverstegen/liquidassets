package org.vaakbenjetebang.model;

import lombok.Data;

@Data
public class WhiskyProduct {
    private String name;
    private double price;
    private double discount;
    private double discountedPrice;

    @Override
    public String toString() {
        return String.format("Name: %s. Original price: %.2f. Discounted price: %.2f.", name, price, discountedPrice);
    }
}
