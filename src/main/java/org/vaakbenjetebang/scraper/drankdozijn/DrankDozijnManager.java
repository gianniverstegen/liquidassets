package org.vaakbenjetebang.scraper.drankdozijn;

import org.openqa.selenium.WebElement;
import org.vaakbenjetebang.model.QueueItem;

import javax.inject.Inject;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class DrankDozijnManager {

    private final DrankDozijnWhiskyProcessor processor;
    private final DrankDozijnWhiskyScraper scraper;
    private final BlockingQueue<QueueItem<WebElement>> scraperToProcessorQueue;
    private final BlockingQueue<QueueItem<WebElement>> processorToConsumerQueue;

    @Inject
    public DrankDozijnManager(DrankDozijnWhiskyProcessor processor, DrankDozijnWhiskyScraper scraper) {
        this.processor = processor;
        this.scraper = scraper;
        this.scraperToProcessorQueue = new LinkedBlockingQueue<>();
        this.processorToConsumerQueue = new LinkedBlockingQueue<>();
    }

    public void start() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(() -> scraper.scrape(scraperToProcessorQueue));
    }
}
