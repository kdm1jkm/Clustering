package com.github.kdm1jkm.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Callable<Map<String, Integer>>> tasks = new ArrayList<>();

        for (String keyWord : new String[]{"밥", "나무위키", "에러", "사과", "죽"}) {
            NamuDoc namuDoc = new NamuDoc(keyWord);
            tasks.add(namuDoc::analyze);
        }

        List<Future<Map<String, Integer>>> results = executorService.invokeAll(tasks);

        for (Future<Map<String, Integer>> result : results) {
            Map<String, Integer> stringIntegerMap = result.get();
            System.out.println(stringIntegerMap);
        }

        executorService.shutdown();
    }
}
