package org.vaakbenjetebang.scraper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.A;
import org.jsoup.nodes.Element;
import org.openqa.selenium.WebElement;
import org.vaakbenjetebang.model.WhiskyProduct;
import org.vaakbenjetebang.scraper.drankdozijn.DrankDozijnWhiskyProcessor;
import org.vaakbenjetebang.scraper.drankdozijn.DrankDozijnWhiskyScraper;
import org.vaakbenjetebang.scraper.gall.GallWhiskyProcessor;
import org.vaakbenjetebang.scraper.gall.GallWhiskyScraper;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ScraperManager {
    private final static Logger log = LogManager.getLogger();
    private final Scraper<Element> gallWhiskyScraper;
    private final Processor<Element> gallWhiskyProcessor;
    private final Scraper<WebElement> drankDozijnWhiskyScraper;
    private final Processor<WebElement> drankDozijnWhiskyProcessor;
    @Inject
    public ScraperManager(GallWhiskyScraper gallWhiskyScraper, GallWhiskyProcessor gallWhiskyProcessor,
                          DrankDozijnWhiskyScraper drankDozijnWhiskyScraper, DrankDozijnWhiskyProcessor drankDozijnWhiskyProcessor) {
        this.gallWhiskyScraper = gallWhiskyScraper;
        this.gallWhiskyProcessor = gallWhiskyProcessor;
        this.drankDozijnWhiskyScraper = drankDozijnWhiskyScraper;
        this.drankDozijnWhiskyProcessor = drankDozijnWhiskyProcessor;

    }

    public void start() throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();
        List<WhiskyProduct> whiskyProducts = new ArrayList<>();
        List<Element> gallWhiskyElements = gallWhiskyScraper.scrape();
        List<WhiskyProduct> gallWhiskyProducts = gallWhiskyProcessor.process(gallWhiskyElements);
        whiskyProducts.addAll(gallWhiskyProducts);

        List<WebElement> drankDozijnWhiskyElements = drankDozijnWhiskyScraper.scrape();
        List<WhiskyProduct> drankDozijnWhiskyProducts = drankDozijnWhiskyProcessor.process(drankDozijnWhiskyElements);
        whiskyProducts.addAll(drankDozijnWhiskyProducts);

        long endTime = System.currentTimeMillis();
        long timeTaken = (endTime - startTime) / 1000;
        log.info("Successfully scraped and processed {} items. Took ~{} seconds.", whiskyProducts.size(), timeTaken);
    }
}
