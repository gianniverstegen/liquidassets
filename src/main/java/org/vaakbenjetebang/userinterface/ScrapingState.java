package org.vaakbenjetebang.userinterface;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


@Singleton
public class ScrapingState {
    private final AtomicLong trieSize;
    private final AtomicBoolean doneScraping;

    @Inject
    public ScrapingState() {
        trieSize = new AtomicLong();
        doneScraping = new AtomicBoolean(false);
    }

    public void incrementCount() {
        trieSize.getAndIncrement();
    }

    public void setDone() {
        doneScraping.set(true);
    }

    public long getTrieSize() {
        return trieSize.get();
    }

    public boolean isDone() {
        return doneScraping.get();
    }
}
