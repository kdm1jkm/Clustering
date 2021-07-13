package com.github.kdm1jkm.clustering;

public interface Doc {
    public WordVec getWordVec();

    public static double getCosineDistance(Doc doc1, Doc doc2) {
        return doc1.getWordVec().calcCosineDistance(doc2.getWordVec());
    }
}
