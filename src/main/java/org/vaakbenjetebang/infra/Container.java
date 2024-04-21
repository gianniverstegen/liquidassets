package org.vaakbenjetebang.infra;

import dagger.Component;
import org.vaakbenjetebang.scraper.ScraperManager;

import javax.inject.Singleton;

@Singleton
@Component()
public interface Container {
    ScraperManager getScraperManager();
}
