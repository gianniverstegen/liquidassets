package org.vaakbenjetebang.scraper.gall;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.vaakbenjetebang.model.QueueItem;
import org.vaakbenjetebang.scraper.Scraper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.inject.Inject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GallWhiskyScraper implements Scraper<Element> {

    private final static Logger log = LogManager.getLogger();
    private final static String GALL_URL = "https://www.gall.nl/whisky/?prefn1=actions&prefv1=Acties%20voor%20Premium%7CActies%20voor%20iedereen";
    private final static String WHISKY_PRODUCT_CLASS_NAME = "ptile";

    @Inject
    public GallWhiskyScraper() {}

    @Override
    public void scrape(BlockingQueue<QueueItem<Element>> outputQueue) {
        long startTime = System.currentTimeMillis();
        HttpClient client = HttpClient.newBuilder().build();

        int start = 0;
        Elements whiskyElementsPerPage = new Elements();
        while (start == 0 || !whiskyElementsPerPage.isEmpty()) {
            String url = GALL_URL + "&start=" + start;

            try {
                Document doc = getDocument(url, client);
                Elements productGrid = doc.getElementsByClass("c-product-grid");

                whiskyElementsPerPage = productGrid.getFirst().getElementsByClass(WHISKY_PRODUCT_CLASS_NAME);

                whiskyElementsPerPage.stream().map(QueueItem::of).forEach(outputQueue::add);
                start += 12;
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch(ExecutionException e) {
                log.error(e.getCause());
            }

        }

        client.close();
        long endTime = System.currentTimeMillis();
        boolean sentinelAdded = false;
        while (!sentinelAdded) {
            sentinelAdded = outputQueue.offer(QueueItem.sentinel());
        }

        log.info("Took ~ {} seconds to scrape.", ((endTime - startTime) / 1000));
    }

    private static Document getDocument(String url, HttpClient client) throws ExecutionException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        CompletableFuture<String> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body);
        String body = response.get();
        return Jsoup.parse(body);
    }
}
