package org.vaakbenjetebang.model;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhiskyProductTest {

    @Test
    public void constructorShouldCreateNonNullId() {
        WhiskyProduct product = new WhiskyProduct();

        assertThat(product.getId()).isNotNull();
    }

    @Test
    public void gettersAndSettersShouldWorkForAllMutableFields() {
        WhiskyProduct product = new WhiskyProduct();
        Website website = mock(Website.class);
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
    public void toStringShouldFormatFieldsUsingTwoDecimalPlaces() {
        Website website = mock(Website.class);
        when(website.toString()).thenReturn("https://example.com/shop");

        WhiskyProduct product = new WhiskyProduct();
        product.setName("Ardbeg Uigeadail");
        product.setPrice(123.456);
        product.setDiscountedPrice(98.1);
        product.setWebsite(website);

        assertThat(product.toString())
                .isEqualTo("Ardbeg Uigeadail. From: 123,46, to: 98,10 at https://example.com/shop.");
    }

    @Test
    public void toStringShouldHandleNullFields() {
        WhiskyProduct product = new WhiskyProduct();

        assertThat(product.toString()).isEqualTo("null. From: 0,00, to: 0,00 at null.");
    }

    @Test
    public void equalsAndHashCodeShouldUseAllFieldsIncludingId() {
        UUID sharedId = UUID.randomUUID();

        WhiskyProduct left = createProduct(sharedId, "Laphroaig", 54.5, 5.0, 49.5, mock(Website.class));
        WhiskyProduct right = createProduct(sharedId, "Laphroaig", 54.5, 5.0, 49.5, left.getWebsite());

        assertThat(left).isEqualTo(right);
        assertThat(left.hashCode()).isEqualTo(right.hashCode());
    }

    @Test
    public void equalsShouldBeReflexiveSymmetricAndRejectDifferentTypesAndNull() {
        WhiskyProduct product = createProduct(UUID.randomUUID(), "Bowmore", 42.0, 2.0, 40.0, mock(Website.class));

        assertThat(product).isEqualTo(product);
        assertThat(product).isNotEqualTo(null);
        assertThat(product).isNotEqualTo("not a product");
    }

    @Test
    public void equalsShouldRejectDifferentIdOrFieldValues() {
        Website website = mock(Website.class);
        UUID id = UUID.randomUUID();

        WhiskyProduct base = createProduct(id, "Talisker", 60.0, 0.0, 60.0, website);
        WhiskyProduct differentId = createProduct(UUID.randomUUID(), "Talisker", 60.0, 0.0, 60.0, website);
        WhiskyProduct differentName = createProduct(id, "Talisker 10", 60.0, 0.0, 60.0, website);
        WhiskyProduct differentPrice = createProduct(id, "Talisker", 61.0, 0.0, 60.0, website);
        WhiskyProduct differentDiscount = createProduct(id, "Talisker", 60.0, 1.0, 60.0, website);
        WhiskyProduct differentDiscountedPrice = createProduct(id, "Talisker", 60.0, 0.0, 59.0, website);
        WhiskyProduct differentWebsite = createProduct(id, "Talisker", 60.0, 0.0, 60.0, mock(Website.class));

        assertThat(base).isNotEqualTo(differentId);
        assertThat(base).isNotEqualTo(differentName);
        assertThat(base).isNotEqualTo(differentPrice);
        assertThat(base).isNotEqualTo(differentDiscount);
        assertThat(base).isNotEqualTo(differentDiscountedPrice);
        assertThat(base).isNotEqualTo(differentWebsite);
    }

    @Test
    public void canEqualShouldReturnTrueForWhiskyProductAndFalseForOtherTypes() {
        WhiskyProduct product = new WhiskyProduct();

        assertThat(product.canEqual(new WhiskyProduct())).isTrue();
        assertThat(product.canEqual(Mockito.mock(Object.class))).isFalse();
    }

    @Test
    public void hashCodeShouldDifferWhenImportantFieldsDiffer() {
        Website website = mock(Website.class);

        WhiskyProduct base = createProduct(UUID.randomUUID(), "Glenfiddich", 45.0, 3.0, 42.0, website);
        WhiskyProduct modified = createProduct(UUID.randomUUID(), "Glenfiddich", 45.0, 3.0, 42.0, website);

        assertThat(base.hashCode()).isNotEqualTo(modified.hashCode());
    }

    private WhiskyProduct createProduct(UUID id, String name, double price, double discount, double discountedPrice, Website website) {
        WhiskyProduct product = new WhiskyProduct();
        setField(product, "id", id);
        product.setName(name);
        product.setPrice(price);
        product.setDiscount(discount);
        product.setDiscountedPrice(discountedPrice);
        product.setWebsite(website);
        return product;
    }

    private static void setField(WhiskyProduct product, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = WhiskyProduct.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(product, value);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Failed to set field " + fieldName, e);
        }
    }
}