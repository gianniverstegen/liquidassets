package org.vaakbenjetebang;

import org.vaakbenjetebang.infra.DaggerContainer;
import org.vaakbenjetebang.model.WhiskyProduct;
import org.vaakbenjetebang.scraper.ScraperManager;
import org.vaakbenjetebang.search.WhiskySuffixTrie;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class LiquidAssets {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ScraperManager scraperManager = DaggerContainer.create().getScraperManager();
        List<WhiskyProduct> whiskyProducts = scraperManager.start();

        WhiskySuffixTrie suffixTrie = new WhiskySuffixTrie();
        suffixTrie.addAll(whiskyProducts);

        Scanner scanner = new Scanner(System.in);

        System.out.println("What whisky do you want to search for?");

        String pattern = scanner.nextLine();

        while (!pattern.equals("q")) {
            List<WhiskyProduct> result = suffixTrie.search(pattern);

            System.out.println("Result: ");
            result.stream().parallel().forEach(System.out::println);
            System.out.println("Search more? Enter q to exit");
            pattern = scanner.nextLine();
        }
    }
}