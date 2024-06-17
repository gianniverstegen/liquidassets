package org.vaakbenjetebang.scraper.gall;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.vaakbenjetebang.model.GallWhiskyProduct;
import org.vaakbenjetebang.model.QueueItem;
import org.vaakbenjetebang.model.WhiskyProduct;
import org.vaakbenjetebang.scraper.Processor;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class GallWhiskyProcessor extends Processor<Element> {
    private final Gson gson = new GsonBuilder().registerTypeAdapter(GallWhiskyProduct.class, new GallWhiskyProduct.GallWhiskyProductDeserializer()).create();

    @Inject
    public GallWhiskyProcessor() {}

    @Override
    public Optional<WhiskyProduct> processItem(Element item) {
        Attributes attributes = item.attributes();
        String attributeContent = attributes.get("data-product");

        if (!attributes.isEmpty()) {
            return Optional.of(gson.fromJson(attributeContent, GallWhiskyProduct.class));
        }

        return Optional.empty();
    }

}
