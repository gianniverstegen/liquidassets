package org.vaakbenjetebang.scraper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaakbenjetebang.model.QueueItem;
import org.vaakbenjetebang.model.WhiskyProduct;
import org.vaakbenjetebang.scraper.drankdozijn.DrankDozijnWhiskyProcessor;
import org.vaakbenjetebang.scraper.drankdozijn.DrankDozijnWhiskyScraper;
import org.vaakbenjetebang.scraper.gall.GallWhiskyProcessor;
import org.vaakbenjetebang.scraper.gall.GallWhiskyScraper;
import org.vaakbenjetebang.search.WhiskySuffixTrie;
import org.vaakbenjetebang.userinterface.ScrapingState;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ScrapingProcessManager {
    private final static Logger log = LogManager.getLogger(ScrapingProcessManager.class);
    private final List<WebsiteManager<?>> websiteManagers;
    private final BlockingQueue<QueueItem<WhiskyProduct>> queue;
    private final ScrapingState state;
    private final WhiskySuffixTrie trie;

    @Inject
    public ScrapingProcessManager(ScrapingState state, WhiskySuffixTrie trie
            ,DrankDozijnWhiskyScraper ddScraper, DrankDozijnWhiskyProcessor ddProcessor, GallWhiskyScraper ggScraper, GallWhiskyProcessor ggProcessor) {
        websiteManagers = List.of(
                new WebsiteManager<>(ddProcessor, ddScraper),
                new WebsiteManager<>(ggProcessor, ggScraper)
        );
        this.queue = new LinkedBlockingQueue<>();

        this.state = state;
        this.trie = trie;
    }

    public void start() {
        long startTime = System.currentTimeMillis();

        List<Future<?>> managerFutures = new ArrayList<>();
        Future<?> populateSuffixTrieFuture;
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            for (WebsiteManager<?> manager : websiteManagers) {
                managerFutures.add(executorService.submit(() -> manager.start(queue)));
            }
            populateSuffixTrieFuture = executorService.submit(() -> populateSuffixTrie(queue, trie, state));
        }


        try {
            for (Future<?> managerFuture : managerFutures) {
                managerFuture.get();
            }
            populateSuffixTrieFuture.get();
        } catch (ExecutionException e) {
            log.error(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long endTime = System.currentTimeMillis();
        long timeTaken = (endTime - startTime) / 1000;

        log.info("Processed all items, took {} ", timeTaken);
    }

    private void populateSuffixTrie(BlockingQueue<QueueItem<WhiskyProduct>> queue, WhiskySuffixTrie trie, ScrapingState state) {
        int sentinelCount = 0;
        try {
            while (sentinelCount < websiteManagers.size()) {
                QueueItem<WhiskyProduct> item = queue.take();

                if (item.isSentinel()) {
                    sentinelCount++;
                    continue;
                }

                WhiskyProduct product = item.getItem();
                trie.add(product);
                state.incrementCount();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        state.setDone();
    }
}
