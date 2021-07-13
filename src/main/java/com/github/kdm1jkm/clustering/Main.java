package com.github.kdm1jkm.clustering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        DocPool docPool = new DocPool();
        List<Callable<Boolean>> load = new ArrayList<>();

        Stream.of("밥", "죽", "바나나", "사과", "에러", "떡", "시계", "책", "종이", "커피", "컴퓨터", "CASIO").forEach(word -> load.add(() -> {
                    docPool.registerDoc(new NamuDoc(word));
                    System.out.println("Complete " + word + "!");
                    return true;
                })
        );

        ExecutorService service = Executors.newFixedThreadPool(5);
        service.invokeAll(load);

        for (Doc doc : docPool.getDocs()) {
            System.out.println(doc.toString());
        }
        System.out.println("==========");
        int size = docPool.getDocs().size();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                System.out.print(docPool.getCosineDistance(i, j));
                System.out.print("\t");
            }
            System.out.println();
        }

        System.out.println("-----------");

        Clusterer clusterer = new Clusterer(docPool, Clusterer.avgSimilarity);
        clusterer.cluster();
        clusterer.getNodes().forEach(node -> {
            node.getDocs().forEach(doc -> {
                System.out.print(doc);
                System.out.print("\t");
            });
            System.out.println();
        });

        List<Node> nodes = Collections.singletonList(clusterer.getRootNode());

        int i = 0;
        List<Node> connectNodes = new ArrayList<>(clusterer.getConnectNodes());
        Collections.reverse(connectNodes);
        for (Node connectNode : connectNodes) {
            List<Node> next = new ArrayList<>();
            for (Node node : nodes) {
                System.out.print(node.getDocs().stream().map(Object::toString).collect(Collectors.joining("   ")));
                System.out.print(" | ");
                if (node == connectNode) {
                    next.addAll(node.getChildNode());
                } else {
                    next.add(node);
                }
            }

            System.out.println(clusterer.getScores().get(i++));

            nodes = next;
            if (nodes.stream().allMatch(node -> (node instanceof DocNode))) {
                System.out.print(next.stream().map(node -> (node.getDocs().get(0).toString())).collect(Collectors.joining(" | ")));
                break;
            }
        }
        service.shutdown();
    }
}
