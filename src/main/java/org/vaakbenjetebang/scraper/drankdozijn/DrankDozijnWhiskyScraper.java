package org.vaakbenjetebang.scraper.drankdozijn;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.C;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.vaakbenjetebang.model.GallWhiskyProduct;
import org.vaakbenjetebang.model.QueueItem;
import org.vaakbenjetebang.scraper.Scraper;

import javax.inject.Inject;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;


public class DrankDozijnWhiskyScraper implements Scraper<WebElement> {
    private final WebDriver driver;
    private final Actions actions;
    private final static Logger log = LogManager.getLogger();
    private final static String URL = "https://drankdozijn.nl/aanbiedingen/whisky";
    private final static By CONTINUE_TO_SITE_BUTTON_IDENTIFIER = By.name("naar_drankdozijn");
    private final static By ALLOW_COOKIE_BUTTON_IDENTIFIER = By.className("cc-allow");
    private final static By LOAD_MORE_WHISKYS_BUTTON_IDENTIFIER = By.id("morebtn");
    private final static By WHISKY_PRODUCT_IDENTIFIER = By.className("product");
    private final static long MS_TO_SLEEP = 200L;

    @Inject
    public DrankDozijnWhiskyScraper() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless=new");
        driver = new ChromeDriver(chromeOptions);
        actions = new Actions(driver);
    }

    @Override
    public void scrape(BlockingQueue<QueueItem<WebElement>> outputQueue) {
        long startTime = System.currentTimeMillis();
        driver.get(URL);
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(ExpectedConditions.visibilityOfElementLocated(CONTINUE_TO_SITE_BUTTON_IDENTIFIER));

        WebElement ageVerifyButton = driver.findElement(CONTINUE_TO_SITE_BUTTON_IDENTIFIER);
        ageVerifyButton.click();

        wait.until(ExpectedConditions.elementToBeClickable(ALLOW_COOKIE_BUTTON_IDENTIFIER));
        WebElement cookieConsentButton = driver.findElement(ALLOW_COOKIE_BUTTON_IDENTIFIER);
        cookieConsentButton.click();

        WebElement loadMoreButton = driver.findElement(LOAD_MORE_WHISKYS_BUTTON_IDENTIFIER);
        loadMoreButton.click();

        Set<WebElement> articles = new HashSet<>();
        int oldSize = -1;

        while (articles.size() > oldSize) {
            oldSize = articles.size();

            actions.sendKeys(Keys.END).perform();
            try {
                Thread.sleep(MS_TO_SLEEP);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            List<WebElement> allCurrentVisibleArticles = driver.findElements(WHISKY_PRODUCT_IDENTIFIER);
            articles.addAll(allCurrentVisibleArticles);
            int sizeDifference = articles.size() - oldSize;
            log.info("Processed {} new items. {} in total", sizeDifference, articles.size());
        }

        long endTime = System.currentTimeMillis();
        log.info("Took ~" + ((endTime - startTime) / 1000) + " seconds to scrape DrankDozijn. Found " + articles.size() + " whiskys.");
        return new ArrayList<>(articles);
    }
}
