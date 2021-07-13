package com.github.kdm1jkm.clustering;

import java.util.ArrayList;
import java.util.List;

public class DocPool {
    private final List<Doc> docs = new ArrayList<>();
    private final List<Double> similarities = new ArrayList<>();

    public List<Doc> getDocs() {
        return docs;
    }

    public List<Double> getSimilarities() {
        return similarities;
    }

    public void registerDoc(Doc doc) {
        docs.forEach(doc1->{
            similarities.add(doc.getWordVec().calcCosine(doc1.getWordVec()));
        });
        docs.add(doc);
    }

    public double getCosineSimilarity(int index1, int index2) {
        if (index1 == index2)
            return 1;

        int idx1 = Math.max(index1, index2);
        int idx2 = Math.min(index1, index2);

        return similarities.get((idx1 - 1) * idx1 / 2 + idx2);
    }

    public double getCosineSimilarity(Doc doc1, Doc doc2) {
        int index1 = docs.indexOf(doc1);
        int index2 = docs.indexOf(doc2);

        if (index1 == -1 || index2 == -1) {
            throw new IllegalArgumentException();
        }

        return getCosineSimilarity(index1, index2);
    }
}
