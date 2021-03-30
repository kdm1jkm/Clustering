package com.github.kdm1jkm.clustering;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class NamuDocPool {
    private final List<NamuDoc> docs;

    public NamuDocPool(int randomSize, String... docs) throws IOException {
        this.docs = new ArrayList<>();
        Arrays.stream(docs).map(NamuDoc::new).forEach(this.docs::add);
        for (int i = 0; i < randomSize; i++) {
            this.docs.add(NamuDoc.getRandomDoc());
        }
    }

    public void analyzeAll() throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();

        executorService.invokeAll(docs.stream().<Callable<Map<String, Integer>>>map(namuDoc -> namuDoc::analyze).collect(Collectors.toList()));

        executorService.shutdown();
    }

    public double[][] getAllCosineSimilarity() throws IOException {
        int len = docs.size();
        double[][] result = new double[len][];

        for (int i = 0; i < len; i++) {
            result[i] = new double[len];
            for (int j = 0; j < len; j++) {
                if (j < i) {
                    result[i][j] = result[j][i];
                } else if (i == j) {
                    result[i][j] = 1;
                } else {
                    result[i][j] = docs.get(i).getCosineSimilarity(docs.get(j));
                }
            }
        }

        return result;
    }

    public List<NamuDoc> getDocs() {
        return Collections.unmodifiableList(docs);
    }

}
