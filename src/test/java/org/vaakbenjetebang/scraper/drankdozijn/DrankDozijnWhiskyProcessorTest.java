package org.vaakbenjetebang.scraper.drankdozijn;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.vaakbenjetebang.model.Website;
import org.vaakbenjetebang.model.WhiskyProduct;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DrankDozijnWhiskyProcessorTest {

    private final DrankDozijnWhiskyProcessor drankDozijnWhiskyProcessor = new DrankDozijnWhiskyProcessor();

    @Test
    public void shouldProcessWebElementsSuccessfully() {
        WebElement root = mock(WebElement.class);
        WebElement titleElement = mock(WebElement.class);
        WebElement priceGroupElement = mock(WebElement.class);

        when(root.findElement(By.className("card-title"))).thenReturn(titleElement);
        when(root.findElements(By.className("price_group"))).thenReturn(Collections.singletonList(priceGroupElement));
        when(titleElement.getText()).thenReturn("Lagavulin 16");
        when(priceGroupElement.getText()).thenReturn("€69,95\n€54,95");

        Optional<WhiskyProduct> result = drankDozijnWhiskyProcessor.processItem(root);

        assertThat(result).isPresent();
        WhiskyProduct product = result.get();
        assertThat(product.getName()).isEqualTo("Lagavulin 16");
        assertThat(product.getWebsite()).isEqualTo(Website.DRANKDOZIJN);
        assertThat(product.getPrice()).isEqualTo(69.95d);
        assertThat(product.getDiscountedPrice()).isEqualTo(54.95d);
        assertThat(product.getDiscount()).isCloseTo(15.0d, within(1e-12));
    }

    @Test
    public void shouldReturnEmptyWhenOriginalPriceCannotBeParsed() {
        WebElement root = mock(WebElement.class);
        WebElement titleElement = mock(WebElement.class);
        WebElement priceGroupElement = mock(WebElement.class);

        when(root.findElement(By.className("card-title"))).thenReturn(titleElement);
        when(root.findElements(By.className("price_group"))).thenReturn(Collections.singletonList(priceGroupElement));
        when(titleElement.getText()).thenReturn("Ardbeg 10");
        when(priceGroupElement.getText()).thenReturn("not-a-number\n€44,95");

        Optional<WhiskyProduct> result = drankDozijnWhiskyProcessor.processItem(root);

        assertThat(result).isEmpty();
    }

    @Test
    public void shouldReturnEmptyWhenDiscountedPriceCannotBeParsed() {
        WebElement root = mock(WebElement.class);
        WebElement titleElement = mock(WebElement.class);
        WebElement priceGroupElement = mock(WebElement.class);

        when(root.findElement(By.className("card-title"))).thenReturn(titleElement);
        when(root.findElements(By.className("price_group"))).thenReturn(Collections.singletonList(priceGroupElement));
        when(titleElement.getText()).thenReturn("Glenlivet 12");
        when(priceGroupElement.getText()).thenReturn("€39,95\nbroken");

        Optional<WhiskyProduct> result = drankDozijnWhiskyProcessor.processItem(root);

        assertThat(result).isEmpty();
    }

    @Test
    public void shouldParseCommaDecimalsAndComputeDiscountPrecisely() {
        WebElement root = mock(WebElement.class);
        WebElement titleElement = mock(WebElement.class);
        WebElement priceGroupElement = mock(WebElement.class);

        when(root.findElement(By.className("card-title"))).thenReturn(titleElement);
        when(root.findElements(By.className("price_group"))).thenReturn(Collections.singletonList(priceGroupElement));
        when(titleElement.getText()).thenReturn("Aberlour A'bunadh");
        when(priceGroupElement.getText()).thenReturn("€79,90\n€59,90");

        Optional<WhiskyProduct> result = drankDozijnWhiskyProcessor.processItem(root);

        assertThat(result).isPresent();
        WhiskyProduct product = result.get();
        assertThat(product.getPrice()).isEqualTo(79.90d);
        assertThat(product.getDiscountedPrice()).isEqualTo(59.90d);
        assertThat(product.getDiscount()).isCloseTo(20.0d, within(1e-12));
    }

    @Test(expected = java.util.NoSuchElementException.class)
    public void shouldFailWhenNoPriceGroupIsPresent() {
        WebElement root = mock(WebElement.class);
        WebElement titleElement = mock(WebElement.class);

        when(root.findElement(By.className("card-title"))).thenReturn(titleElement);
        when(root.findElements(By.className("price_group"))).thenReturn(Collections.emptyList());
        when(titleElement.getText()).thenReturn("No price product");

        drankDozijnWhiskyProcessor.processItem(root);
    }

    private static org.assertj.core.data.Offset<Double> within(double offset) {
        return org.assertj.core.data.Offset.offset(offset);
    }
}