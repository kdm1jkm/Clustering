package com.github.kdm1jkm.clustering;

import java.util.Map;

public class WordVec {
    private final Map<String, Integer> data;

    public WordVec(Map<String, Integer> data) {
        this.data = data;
    }

    public Map<String, Integer> getData() {
        return data;
    }

    public double calcCosineDistance(WordVec wordvec) {
        return calcCosineDistance(this, wordvec);
    }

    public static double calcCosineDistance(WordVec vec1, WordVec vec2) {
        if (vec1 == vec2) return 1;

        int sum = 0;

        for (String key : vec1.data.keySet()) {
            sum += vec1.data.get(key) * vec2.data.getOrDefault(key, 0);
        }

        double len1 = Math.sqrt(vec1.data.values().stream().mapToInt(num -> num * num).sum());
        double len2 = Math.sqrt(vec2.data.values().stream().mapToInt(num -> num * num).sum());

        return 1 - sum / (len1 * len2);
    }
}
