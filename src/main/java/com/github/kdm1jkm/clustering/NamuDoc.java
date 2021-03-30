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

public class NamuDoc {
    private final String url;
    private final String keyWord;
    private Map<String, Integer> analyzedData = null;
    private String content = null;

    public NamuDoc(String keyWord) {
        url = String.format("https://namu.wiki/w/%s", keyWord);
        this.keyWord = keyWord;
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

    public String getContent() throws IOException {
        if (content != null) return content;

        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (HttpStatusException e) {
            throw new IllegalStateException(String.format("Provided keyWord %s is not valid.", keyWord));
        }
        return content = extractTextFromDocument(doc);
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

        return analyzedData;
    }

    public double getCosineSimilarity(NamuDoc other) throws IOException {
        if (other == this) return 1;
        int sum = 0;
        Map<String, Integer> analyzed1 = analyze();
        Map<String, Integer> analyzed2 = other.analyze();

        for (String key : analyzed1.keySet()) {
            sum += analyzed1.get(key) * analyzed2.getOrDefault(key, 0);
        }

        double len1 = Math.sqrt(analyzed1.values().stream().mapToInt(num -> num * num).sum());
        double len2 = Math.sqrt(analyzed2.values().stream().mapToInt(num -> num * num).sum());

        return sum / (len1 * len2);
    }

    public String getKeyWord() {
        return keyWord;
    }

    @Override
    public String toString() {
        try {
            return analyze().entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toList()).toString();
        } catch (IOException e) {
            return "";
        }
    }
}
