package org.vaakbenjetebang.model;

import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotEquals;

public class WhiskyProductTest {

    @Test
    public void constructorShouldInitializeUniqueId() {
        WhiskyProduct product1 = new WhiskyProduct();
        WhiskyProduct product2 = new WhiskyProduct();

        assertThat(product1.getId()).isNotNull();
        assertThat(product2.getId()).isNotNull();
        assertThat(product1.getId()).isNotEqualTo(product2.getId());
    }

    @Test
    public void gettersAndSettersShouldWorkForAllMutableFields() {
        WhiskyProduct product = new WhiskyProduct();
        UUID id = product.getId();
        Website website = null;

        product.setName("Lagavulin 16");
        product.setPrice(89.99d);
        product.setDiscount(10.0d);
        product.setDiscountedPrice(80.99d);
        product.setWebsite(website);

        assertThat(product.getName()).isEqualTo("Lagavulin 16");
        assertThat(product.getPrice()).isEqualTo(89.99d);
        assertThat(product.getDiscount()).isEqualTo(10.0d);
        assertThat(product.getDiscountedPrice()).isEqualTo(80.99d);
        assertThat(product.getWebsite()).isNull();
        assertThat(product.getId()).isEqualTo(id);
    }

    @Test
    public void equalsShouldBeBasedOnAllFieldsIncludingId() {
        WhiskyProduct left = new WhiskyProduct();
        left.setName("Ardbeg");
        left.setPrice(74.50d);
        left.setDiscount(5.0d);
        left.setDiscountedPrice(70.00d);
        left.setWebsite(null);

        WhiskyProduct right = new WhiskyProduct();
        right.setName("Ardbeg");
        right.setPrice(74.50d);
        right.setDiscount(5.0d);
        right.setDiscountedPrice(70.00d);
        right.setWebsite(null);

        assertThat(left).isNotEqualTo(right);
        assertThat(left).isEqualTo(left);
        assertThat(left.equals(null)).isFalse();
        assertThat(left.equals("not a product")).isFalse();
        assertThat(left.canEqual(right)).isTrue();
    }

    @Test
    public void hashCodeShouldBeStableAndDependOnState() {
        WhiskyProduct product = new WhiskyProduct();
        product.setName("Talisker");
        product.setPrice(55.25d);
        product.setDiscount(2.5d);
        product.setDiscountedPrice(52.75d);
        product.setWebsite(null);

        int firstHash = product.hashCode();
        int secondHash = product.hashCode();

        assertThat(firstHash).isEqualTo(secondHash);

        WhiskyProduct sameStateDifferentId = new WhiskyProduct();
        sameStateDifferentId.setName("Talisker");
        sameStateDifferentId.setPrice(55.25d);
        sameStateDifferentId.setDiscount(2.5d);
        sameStateDifferentId.setDiscountedPrice(52.75d);
        sameStateDifferentId.setWebsite(null);

        assertThat(product).isNotEqualTo(sameStateDifferentId);
        assertNotEquals(firstHash, sameStateDifferentId.hashCode());
    }

    @Test
    public void toStringShouldFormatValuesAndIncludeWebsite() {
        WhiskyProduct product = new WhiskyProduct();
        product.setName("Glenmorangie");
        product.setPrice(12.345d);
        product.setDiscountedPrice(9.876d);
        product.setWebsite(null);

        assertThat(product.toString()).isEqualTo("Glenmorangie. From: 12,35, to: 9,88 at null.");
    }
}