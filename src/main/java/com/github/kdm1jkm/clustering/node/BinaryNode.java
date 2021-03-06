package com.github.kdm1jkm.clustering.node;

import com.github.kdm1jkm.clustering.doc.Doc;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BinaryNode implements Node {
    public final Node left, right;

    public BinaryNode(Node left, Node right) {
        Objects.requireNonNull(left);
        Objects.requireNonNull(right);
        this.left = left;
        this.right = right;
    }

    @Override
    public List<Doc> getDocs() {
        return Stream.concat(left.getDocs().stream(), right.getDocs().stream()).collect(Collectors.toList());
    }

    @Override
    public List<Node> getChildNode() {
        return Arrays.asList(left, right);
    }

    @Override
    public String toString() {
        return "{" +
                "\"left\": " + left +
                ", \"right\": " + right +
                '}';
    }
}
