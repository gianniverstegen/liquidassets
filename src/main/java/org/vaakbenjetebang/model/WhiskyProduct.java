package org.vaakbenjetebang.model;

import lombok.Data;

import java.util.UUID;

@Data
public class WhiskyProduct {
    private String name;
    private double price;
    private double discount;
    private double discountedPrice;
    private Website website;
    private final UUID id;

    public WhiskyProduct() {
        this.id = UUID.randomUUID();
    }


    @Override
    public String toString() {
        return String.format("Name: %s. Original price: %.2f. Discounted price: %.2f. Found on %s.", name, price, discountedPrice, website);
    }
}
