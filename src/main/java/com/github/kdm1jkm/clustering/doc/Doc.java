package com.github.kdm1jkm.clustering.doc;

import com.github.kdm1jkm.clustering.WordVec;

public interface Doc {
    static double getCosineDistance(Doc doc1, Doc doc2) {
        return doc1.getWordVec().calcCosineDistance(doc2.getWordVec());
    }

    WordVec getWordVec();
}
