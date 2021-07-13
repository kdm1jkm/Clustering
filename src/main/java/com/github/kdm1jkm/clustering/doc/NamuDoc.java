package com.github.kdm1jkm.clustering.doc;

import com.github.kdm1jkm.clustering.WordVec;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static NamuDoc getRandomDoc() {
        try {
            Document doc = Jsoup.connect("https://namu.wiki/random").userAgent("Mozilla").get();
            String title = doc.select("title").text();
            NamuDoc namuDoc = new NamuDoc(title.substring(0, title.length() - 7));
            namuDoc.content = extractTextFromDocument(doc);
            return namuDoc;
        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    private static String extractTextFromDocument(Document doc) {
        StringBuilder builder = new StringBuilder();
        Elements contents = doc.select("div.wiki-heading-content");
        for (Element content : contents) {
            content.children().eachText().forEach(builder::append);
        }

        return builder.toString();
    }

    public String getWebpage() {
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
        return keyWord;
    }

    @Override
    public WordVec getWordVec() {
        return wordVec;
    }
}
