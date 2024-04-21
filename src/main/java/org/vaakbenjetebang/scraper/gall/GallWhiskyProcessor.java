package org.vaakbenjetebang.scraper.gall;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.vaakbenjetebang.model.GallWhiskyProduct;
import org.vaakbenjetebang.model.WhiskyProduct;
import org.vaakbenjetebang.scraper.Processor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class GallWhiskyProcessor implements Processor<Element> {
    private final static Logger log = LogManager.getLogger();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(GallWhiskyProduct.class, new GallWhiskyProduct.GallWhiskyProductDeserializer()).create();

    @Inject
    public GallWhiskyProcessor() {}

    @Override
    public List<WhiskyProduct> process(List<Element> elements) {
        long startTime = System.currentTimeMillis();
        List<WhiskyProduct> whiskyProducts = new ArrayList<>();
        for (Element element : elements) {
            Attributes attributes = element.attributes();
            String attributeContent = attributes.get("data-product");
            if (!attributes.isEmpty()) {
                whiskyProducts.add(gson.fromJson(attributeContent, GallWhiskyProduct.class));
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("Took ~" + ((endTime - startTime) / 1000) + " seconds to process Gall's entries.");
        return whiskyProducts;
    }
}
