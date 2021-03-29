package com.github.kdm1jkm.clustering;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NamuDoc {
    private final String url;
    private final String keyWord;
    private Map<String, Integer> analyzedData = null;
    private String content = null;

    public NamuDoc(String keyWord) {
        url = String.format("https://namu.wiki/w/%s", keyWord);
        this.keyWord = keyWord;
    }

    public String getContent() throws IOException {
        if (content != null) return content;

        StringBuilder builder = new StringBuilder();

        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (HttpStatusException e) {
            throw new IllegalStateException(String.format("Provided keyWord %s is not valid.", keyWord));
        }
        Elements contents = doc.select("div.wiki-heading-content");
        for (Element content : contents) {
            content.children().eachText().forEach(builder::append);
        }

        content = builder.toString();
        return content;
    }

    public Map<String, Integer> analyze() throws IOException {
        if (analyzedData != null) return analyzedData;

        analyzedData = new HashMap<>();
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        KomoranResult komoranResult = komoran.analyze(getContent());
        List<String> morphs = komoranResult.getMorphesByTags("NNG");

        for (String morph : morphs) {
            analyzedData.put(morph, analyzedData.getOrDefault(morph, 0) + 1);
        }

//        System.out.println(analyzedData.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toList()));

        return analyzedData;
    }
}
