package org.vaakbenjetebang.model;


import com.google.gson.*;

import java.lang.reflect.Type;

public class GallWhiskyProduct extends WhiskyProduct {
    private GallWhiskyProduct() {
        super();
    }

    public static class GallWhiskyProductDeserializer implements JsonDeserializer<GallWhiskyProduct> {
        @Override
        public GallWhiskyProduct deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            String name = jsonObject.get("name").getAsString();

            double price = jsonObject.get("price").getAsDouble();
            double discount = jsonObject.get("discount").getAsDouble();

            double discountedPrice = price - discount;

            GallWhiskyProduct gallWhiskyProduct = new GallWhiskyProduct();
            gallWhiskyProduct.setName(name);
            gallWhiskyProduct.setPrice(price);
            gallWhiskyProduct.setDiscount(discount);
            gallWhiskyProduct.setDiscountedPrice(discountedPrice);
            gallWhiskyProduct.setWebsite(Website.GALL);
            return gallWhiskyProduct;
        }
    }
}
