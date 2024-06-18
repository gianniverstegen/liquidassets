package org.vaakbenjetebang.scraper.drankdozijn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.vaakbenjetebang.model.QueueItem;
import org.vaakbenjetebang.model.Website;
import org.vaakbenjetebang.model.WhiskyProduct;
import org.vaakbenjetebang.scraper.Processor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class DrankDozijnWhiskyProcessor extends Processor<WebElement> {

    private final static Logger log = LogManager.getLogger(DrankDozijnWhiskyProcessor.class);

    @Inject
    public DrankDozijnWhiskyProcessor() {}

    @Override
    public Optional<WhiskyProduct> processItem(WebElement element) {
        String name = element.findElement(By.className("card-title")).getText();
        List<WebElement> priceInfo = element.findElements(By.className("price_group"));

        String[] prices = priceInfo.getFirst().getText().split("\n");

        WhiskyProduct whiskyProduct = new WhiskyProduct();

        whiskyProduct.setName(name);
        whiskyProduct.setWebsite(Website.DRANKDOZIJN);

        try {
            String originalPriceAsString = prices[0].replace("€", "").replace(",", ".");
            double originalPrice = Double.parseDouble(originalPriceAsString);

            String discountedPriceAsString = prices[1].replace("€", "").replace(",", ".");
            double discountedPrice = Double.parseDouble(discountedPriceAsString);

            whiskyProduct.setPrice(originalPrice);
            whiskyProduct.setDiscountedPrice(discountedPrice);

            whiskyProduct.setDiscount(originalPrice - discountedPrice);
        } catch (NumberFormatException e) {
            log.error(e);
            return Optional.empty();
        }


        return Optional.of(whiskyProduct);
    }
}
