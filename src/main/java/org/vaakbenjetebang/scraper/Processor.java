package org.vaakbenjetebang.scraper;

import org.vaakbenjetebang.model.WhiskyProduct;

import java.util.List;

public interface Processor<T> {
    List<WhiskyProduct> process(List<T> elements);
}
