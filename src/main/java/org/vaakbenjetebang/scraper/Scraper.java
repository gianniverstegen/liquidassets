package org.vaakbenjetebang.scraper;

import org.vaakbenjetebang.model.WhiskyProduct;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface Scraper<T> {
    List<T> scrape() throws InterruptedException, ExecutionException;
}
