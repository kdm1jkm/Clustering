package com.github.kdm1jkm.clustering;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

public class NamuDocPool {
    private final List<NamuDoc> docs;
    public int threadNum = 5;
    private double[][] similarity = null;

    public NamuDocPool(int randomSize, String... docs) throws IOException {
        this.docs = new ArrayList<>();
        Arrays.stream(docs).map(NamuDoc::new).forEach(this.docs::add);
        for (int i = 0; i < randomSize; i++) {
            this.docs.add(NamuDoc.getRandomDoc());
        }
    }

    public NamuDocPool(NamuDoc... namuDocs) {
        docs = Arrays.asList(namuDocs.clone());
    }

    public void analyzeAll() {
        Semaphore semaphore = new Semaphore(threadNum);

        List<Thread> threads = new ArrayList<>();
        List<NamuDoc> fail = new ArrayList<>();

        for (NamuDoc doc : docs) {
            Thread thread = new Thread(() -> {
                try {
                    semaphore.acquire();
                    doc.getContent();
                } catch (IOException e) {
                    fail.add(doc);
                } catch (InterruptedException e) {
                    return;
                }
                semaphore.release();
            });

            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        fail.forEach(docs::remove);

        for (NamuDoc doc : docs) {
            try {
                doc.analyze();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public double[][] getAllCosineSimilarity() throws IOException {
        if (similarity != null) return similarity;

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

        similarity = result;

        return result;
    }

    public List<NamuDoc> getDocs() {
        return Collections.unmodifiableList(docs);
    }

}
