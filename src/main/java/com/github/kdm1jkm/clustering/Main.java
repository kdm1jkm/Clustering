package com.github.kdm1jkm.clustering;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        DocPool docPool = new DocPool();
        Arrays.asList("밥", "죽", "바나나", "사과", "에러").forEach(word -> {
            docPool.registerDoc(new NamuDoc(word));
        });

        for (Doc doc : docPool.getDocs()) {
            System.out.println(doc.toString());
        }
        System.out.println("==========");
        int size = docPool.getDocs().size();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(docPool.getCosineSimilarity(i, j));
                System.out.print("\t");
            }
            System.out.println();
        }
    }
}
