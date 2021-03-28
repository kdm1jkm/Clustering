package com.github.kdm1jkm.clustering;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;

public class NamuDoc {
    private final String url;
    private final String keyWord;
    private String content = null;

    public NamuDoc(String keyWord) throws MalformedURLException {
        url = String.format("https://namu.wiki/w/%s", keyWord);
        this.keyWord = keyWord;
    }

    public String getContent() throws IOException {
        if (content == null) {
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
        }
        return content;
    }
}
