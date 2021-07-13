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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NamuDoc implements Doc {
    private final String url;
    private final String keyWord;
    private final WordVec wordVec;
    private String content;

    public NamuDoc(String keyWord) {
        url = String.format("https://namu.wiki/w/%s", keyWord);
        this.keyWord = keyWord;
        wordVec = new WordVec(analyze());
    }

    public static NamuDoc getRandomDoc() throws IOException {
        Document doc = Jsoup.connect("https://namu.wiki/random").get();
        String title = doc.select("title").text();
        NamuDoc namuDoc = new NamuDoc(title.substring(0, title.length() - 7));
        namuDoc.content = extractTextFromDocument(doc);

        return namuDoc;
    }

    private static String extractTextFromDocument(Document doc) {
        StringBuilder builder = new StringBuilder();
        Elements contents = doc.select("div.wiki-heading-content");
        for (Element content : contents) {
            content.children().eachText().forEach(builder::append);
        }

        return builder.toString();
    }

    public String getWebpage()  {
        if (content != null)
            return content;

        Document doc;
        try {
            doc = Jsoup
                    .connect(url)
                    .userAgent("Mozilla")
                    .get();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new IllegalStateException(String.format("Provided keyWord %s is not valid.", keyWord));
        }
        return content = extractTextFromDocument(doc);
    }

    private Map<String, Integer> analyze() {
        Map<String, Integer> analyzedData = new HashMap<>();
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        String webpage;
        webpage = getWebpage();
        KomoranResult komoranResult = komoran.analyze(webpage);
        List<String> morphs = komoranResult.getMorphesByTags("NNG");

        for (String morph : morphs) {
            analyzedData.put(morph, analyzedData.getOrDefault(morph, 0) + 1);
        }

        return analyzedData;
    }

    public String getKeyWord() {
        return keyWord;
    }

    @Override
    public String toString() {
        return analyze().entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList()).toString();
    }

    @Override
    public WordVec getWordVec() {
        return wordVec;
    }
}
