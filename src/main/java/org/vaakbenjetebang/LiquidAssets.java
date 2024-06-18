package org.vaakbenjetebang;

import org.vaakbenjetebang.infra.Container;
import org.vaakbenjetebang.infra.DaggerContainer;
import org.vaakbenjetebang.scraper.ScrapingProcessManager;
import org.vaakbenjetebang.userinterface.Prompt;

import java.util.concurrent.*;

public class LiquidAssets {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Container container = DaggerContainer.create();
        ScrapingProcessManager scrapingProcessManager = container.getScraperManager();
        Prompt prompt = container.getPrompt();

        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<?> scraping = executorService.submit(scrapingProcessManager::start);
            Future<?> prompting = executorService.submit(prompt::startPrompt);

            prompting.get();
            executorService.shutdownNow();
            scraping.get();
        }
    }
}