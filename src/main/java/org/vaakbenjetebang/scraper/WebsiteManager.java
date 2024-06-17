package org.vaakbenjetebang.scraper;

import org.vaakbenjetebang.model.QueueItem;
import org.vaakbenjetebang.model.WhiskyProduct;

import javax.inject.Inject;
import java.util.concurrent.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebsiteManager<T> {
    private final static Logger log = LogManager.getLogger();
    private final Processor<T> processor;
    private final Scraper<T> scraper;
    private final BlockingQueue<QueueItem<T>> scraperToProcessorQueue;

    @Inject
    public WebsiteManager(Processor<T> processor, Scraper<T> scraper) {
        this.processor = processor;
        this.scraper = scraper;
        this.scraperToProcessorQueue = new LinkedBlockingQueue<>();
    }

    public void start(BlockingQueue<QueueItem<WhiskyProduct>> processorToConsumerQueue) {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<?> scraperRes = executorService.submit(() -> scraper.scrape(scraperToProcessorQueue));
            Future<?> processorRes = executorService.submit(() -> processor.process(scraperToProcessorQueue, processorToConsumerQueue));

            scraperRes.get();
            processorRes.get();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            log.error(e);
        }
    }
}
