package org.vaakbenjetebang.scraper.drankdozijn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.vaakbenjetebang.model.WhiskyProduct;
import org.vaakbenjetebang.scraper.Processor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrankDozijnWhiskyProcessor implements Processor<WebElement> {

    private final static Logger log = LogManager.getLogger();

    @Inject
    public DrankDozijnWhiskyProcessor() {}

    @Override
    public List<WhiskyProduct> process(List<WebElement> elements) {
        long startTime = System.currentTimeMillis();
        List<WhiskyProduct> whiskyProducts = new ArrayList<>();
        for (WebElement element : elements) {
            String name = element.findElement(By.className("card-title")).getText();
            List<WebElement> priceInfo = element.findElements(By.className("price_group"));

            String[] prices = priceInfo.getFirst().getText().split("\n");

            WhiskyProduct whiskyProduct = getWhiskyProduct(prices, name);

            whiskyProducts.add(whiskyProduct);
        }

        long endTime = System.currentTimeMillis();
        log.info("Took ~" + ((endTime - startTime) / 1000) + " seconds to process DrankDozijn's entries.");
        return whiskyProducts;
    }

    private static WhiskyProduct getWhiskyProduct(String[] prices, String name) {
        String originalPriceAsString = prices[0].replace("€", "").replace(",", ".");
        double originalPrice = Double.parseDouble(originalPriceAsString);

        String discountedPriceAsString = prices[1].replace("€", "").replace(",", ".");
        double discountedPrice = Double.parseDouble(discountedPriceAsString);

        WhiskyProduct whiskyProduct = new WhiskyProduct();
        whiskyProduct.setName(name);

        whiskyProduct.setPrice(originalPrice);
        whiskyProduct.setDiscountedPrice(discountedPrice);

        whiskyProduct.setDiscount(originalPrice - discountedPrice);
        return whiskyProduct;
    }
}
