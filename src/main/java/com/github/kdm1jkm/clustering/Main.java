package com.github.kdm1jkm.clustering;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        NamuDocPool namuDocs = new NamuDocPool(0, "밥", "죽", "사과", "바나나", "에러");
        namuDocs.analyzeAll();
        for (NamuDoc doc : namuDocs.getDocs()) {
            System.out.println(doc.getKeyWord());
        }
        System.out.println("==========");
        for (double[] line : namuDocs.getAllCosineSimilarity()) {
            for (double value : line) {
                System.out.print(value);
                System.out.print("\t");
            }
            System.out.println();
        }
    }
}
