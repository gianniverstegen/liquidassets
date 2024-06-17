package org.vaakbenjetebang.infra;

import dagger.Component;
import org.vaakbenjetebang.scraper.ScrapingProcessManager;
import org.vaakbenjetebang.userinterface.Prompt;

import javax.inject.Singleton;

@Singleton
@Component(modules = UserInterfaceModule.class)
public interface Container {
    ScrapingProcessManager getScraperManager();
    Prompt getPrompt();
}
