package org.vaakbenjetebang.scraper;

import org.vaakbenjetebang.model.QueueItem;
import org.vaakbenjetebang.model.WhiskyProduct;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public interface Processor<T> {
    List<WhiskyProduct> process(BlockingQueue<QueueItem<T>> queue);
}
