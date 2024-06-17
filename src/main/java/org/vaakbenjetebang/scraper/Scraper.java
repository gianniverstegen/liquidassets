package org.vaakbenjetebang.scraper;

import org.vaakbenjetebang.model.QueueItem;
import org.vaakbenjetebang.model.WhiskyProduct;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

public interface Scraper<T> {
    void scrape(BlockingQueue<QueueItem<T>> outputQueue);
}
