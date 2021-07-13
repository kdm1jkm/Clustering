package com.github.kdm1jkm.clustering;

import com.github.kdm1jkm.clustering.doc.Doc;
import com.github.kdm1jkm.clustering.doc.DocPool;
import com.github.kdm1jkm.clustering.node.BinaryNode;
import com.github.kdm1jkm.clustering.node.DocNode;
import com.github.kdm1jkm.clustering.node.Node;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Clusterer {
    public static final NodeComparer minSimilarity = ((docPool, node1, node2) ->
            node1.getDocs().stream()
                    .flatMapToDouble(doc1 -> node2.getDocs().stream().mapToDouble(doc2 -> Doc.getCosineDistance(doc1, doc2)))
                    .min().orElse(0)
    );
    public static final NodeComparer maxSimilarity = ((docPool, node1, node2) ->
            node1.getDocs().stream()
                    .flatMapToDouble(doc1 -> node2.getDocs().stream().mapToDouble(doc2 -> Doc.getCosineDistance(doc1, doc2)))
                    .max().orElse(0)
    );
    public static final NodeComparer avgSimilarity = ((docPool, node1, node2) ->
            node1.getDocs().stream()
                    .flatMapToDouble(doc1 -> node2.getDocs().stream().mapToDouble(doc2 -> Doc.getCosineDistance(doc1, doc2)))
                    .average().orElse(0)
    );
    public final DocPool docPool;
    private final NodeComparer nodeComparer;
    private final List<Node> connectNodes = new ArrayList<>();

    public List<Double> getScores() {
        return scores;
    }

    private final List<Double> scores = new ArrayList<>();
    private List<Node> nodes = new ArrayList<>();
    private Node rootNode;

    public Clusterer(DocPool docPool, NodeComparer nodeComparer) {
        this.docPool = docPool;
        this.nodeComparer = nodeComparer;
        nodes.addAll(docPool.getDocs().stream().map(DocNode::new).collect(Collectors.toList()));
    }

    public List<Node> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public Node getRootNode() {
        return rootNode;
    }

    private void connectMostSimilar() {
        Node[] similarNode = new Node[2];
        double similarity = 100;

        int size = nodes.size();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < i; j++) {
                double newSimilarity = nodeComparer.compareNode(docPool, nodes.get(i), nodes.get(j));
                if (similarity > newSimilarity) {
                    similarity = newSimilarity;
                    similarNode = new Node[]{nodes.get(i), nodes.get(j)};
                }
            }
        }

        nodes.remove(similarNode[0]);
        nodes.remove(similarNode[1]);

        BinaryNode connectNode = new BinaryNode(similarNode[0], similarNode[1]);
        nodes.add(connectNode);
        connectNodes.add(connectNode);
        scores.add(IntStream.range(0, docPool.getDocs().size())
                .mapToDouble(this::calcScore)
                .average().orElse(Double.NaN));
    }

    private double calcCohesion(int index) {
        Doc doc = docPool.getDocs().get(index);
        List<Doc> ourDocs = nodes.stream()
                .filter(node -> node.getDocs().contains(doc))
                .flatMap(node -> node.getDocs().stream())
                .collect(Collectors.toList());

        return ourDocs.stream().mapToDouble(ourDoc -> Doc.getCosineDistance(doc, ourDoc)).average().orElse(Double.NaN);
    }

    private double calcSeparation(int index) {
        Doc doc = docPool.getDocs().get(index);
        List<Doc> theirDocs = nodes.stream()
                .filter(node -> !node.getDocs().contains(doc))
                .flatMap(node -> node.getDocs().stream())
                .collect(Collectors.toList());

        return theirDocs.stream().mapToDouble(ourDoc -> Doc.getCosineDistance(doc, ourDoc)).average().orElse(Double.NaN);
    }

    public double calcScore(int index) {
        double separation = calcSeparation(index);
        double cohesion = calcCohesion(index);
        return (separation - cohesion) / Math.max(separation, cohesion);
    }

    public List<Node> getConnectNodes() {
        return connectNodes;
    }

    public void cluster() {
        while (nodes.size() > 1) {
            connectMostSimilar();
        }

        int index = 0;
        double score = -100;

        for (int i = 0; i < scores.size(); i++) {
            double newScore = scores.get(i);
            if (score < newScore) {
                score = newScore;
                index = i;
            }
        }

        rootNode = connectNodes.get(connectNodes.size() - 1);
        if (index == connectNodes.size() - 1) return;

        Set<Node> nodeToSeparate = new HashSet<>(connectNodes.subList(index + 1, connectNodes.size()));
        AtomicInteger seperated = new AtomicInteger();

        do {
            seperated.set(0);
            nodes = nodes.stream().flatMap(node -> {
                if (nodeToSeparate.contains(node)) {
                    seperated.getAndIncrement();
                    return node.getChildNode().stream();
                } else {
                    return Stream.of(node);
                }
            }).collect(Collectors.toList());
        } while (seperated.get() != 0);
    }


    @FunctionalInterface
    public interface NodeComparer {
        double compareNode(DocPool docpool, Node node1, Node node2);
    }
}
