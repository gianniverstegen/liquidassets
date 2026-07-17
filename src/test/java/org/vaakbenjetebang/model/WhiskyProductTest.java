package org.vaakbenjetebang.model;

import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class WhiskyProductTest {

    @Test
    public void constructorShouldAssignNonNullUniqueId() {
        WhiskyProduct first = new WhiskyProduct();
        WhiskyProduct second = new WhiskyProduct();

        assertThat(first.getId()).isNotNull();
        assertThat(second.getId()).isNotNull();
        assertThat(first.getId()).isNotEqualTo(second.getId());
    }

    @Test
    public void shouldExposeGettersAndSetters() {
        WhiskyProduct product = new WhiskyProduct();
        Website website = org.mockito.Mockito.mock(Website.class);
        UUID id = product.getId();

        product.setName("Lagavulin 16");
        product.setPrice(89.99);
        product.setDiscount(10.0);
        product.setDiscountedPrice(79.99);
        product.setWebsite(website);

        assertThat(product.getName()).isEqualTo("Lagavulin 16");
        assertThat(product.getPrice()).isEqualTo(89.99);
        assertThat(product.getDiscount()).isEqualTo(10.0);
        assertThat(product.getDiscountedPrice()).isEqualTo(79.99);
        assertThat(product.getWebsite()).isSameAs(website);
        assertThat(product.getId()).isEqualTo(id);
    }

    @Test
    public void equalsAndHashCodeShouldDependOnAllFieldsIncludingId() {
        WhiskyProduct left = new WhiskyProduct();
        WhiskyProduct right = new WhiskyProduct();

        Website website = org.mockito.Mockito.mock(Website.class);

        left.setName("Ardbeg");
        left.setPrice(55.0);
        left.setDiscount(5.0);
        left.setDiscountedPrice(50.0);
        left.setWebsite(website);

        right.setName("Ardbeg");
        right.setPrice(55.0);
        right.setDiscount(5.0);
        right.setDiscountedPrice(50.0);
        right.setWebsite(website);

        assertThat(left.equals(left)).isTrue();
        assertThat(left.equals(right)).isFalse();
        assertThat(right.equals(left)).isFalse();
        assertThat(left.equals(null)).isFalse();
        assertThat(left.equals("not a whisky product")).isFalse();
        assertThat(left.hashCode()).isNotEqualTo(right.hashCode());

        WhiskyProduct sameAsLeft = new WhiskyProduct();
        setId(sameAsLeft, left.getId());
        sameAsLeft.setName(left.getName());
        sameAsLeft.setPrice(left.getPrice());
        sameAsLeft.setDiscount(left.getDiscount());
        sameAsLeft.setDiscountedPrice(left.getDiscountedPrice());
        sameAsLeft.setWebsite(left.getWebsite());

        assertThat(left.equals(sameAsLeft)).isTrue();
        assertThat(sameAsLeft.equals(left)).isTrue();
        assertThat(left.hashCode()).isEqualTo(sameAsLeft.hashCode());
    }

    @Test
    public void toStringShouldFormatAllFields() {
        WhiskyProduct product = new WhiskyProduct();
        Website website = org.mockito.Mockito.mock(Website.class);

        product.setName("Talisker 10");
        product.setPrice(42.5);
        product.setDiscountedPrice(35.0);
        product.setWebsite(website);

        assertThat(product.toString())
                .isEqualTo(String.format("%s. From: %.2f, to: %.2f at %s.",
                        "Talisker 10", 42.5, 35.0, website));
    }

    private static void setId(WhiskyProduct product, UUID id) {
        try {
            java.lang.reflect.Field field = WhiskyProduct.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(product, id);
        } catch (Exception e) {
            throw new AssertionError("Failed to set id via reflection", e);
        }
    }
}