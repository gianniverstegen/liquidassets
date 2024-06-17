package org.vaakbenjetebang.scraper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaakbenjetebang.model.QueueItem;
import org.vaakbenjetebang.model.WhiskyProduct;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public abstract class Processor<T> {

    private final static Logger log = LogManager.getLogger();
    public void process(BlockingQueue<QueueItem<T>> inputQueue, BlockingQueue<QueueItem<WhiskyProduct>> outputQueue) {
        long startTime = System.currentTimeMillis();
        boolean running = true;
        try {
            while (running) {
                QueueItem<T> item = inputQueue.take();

                if (item.isSentinel()) {
                    running = false;
                    continue;
                }
                processItem(item.getItem()).ifPresent((result) -> {
                    boolean added = false;
                    while (!added) {
                        added = outputQueue.offer(QueueItem.of(result));
                    }
                });
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        boolean sentinelAdded = false;
        while (!sentinelAdded) {
            sentinelAdded = outputQueue.offer(QueueItem.sentinel());
        }
        long endTime = System.currentTimeMillis();
        log.info("Took ~" + ((endTime - startTime) / 1000) + " seconds to process entries.");
    }

    public abstract Optional<WhiskyProduct> processItem(T item);
}
