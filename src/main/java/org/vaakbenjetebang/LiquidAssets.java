package org.vaakbenjetebang;

import org.vaakbenjetebang.infra.DaggerContainer;
import org.vaakbenjetebang.scraper.ScraperManager;

import java.util.concurrent.ExecutionException;

public class LiquidAssets {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ScraperManager scraperManager = DaggerContainer.create().getScraperManager();
        scraperManager.start();
    }
}