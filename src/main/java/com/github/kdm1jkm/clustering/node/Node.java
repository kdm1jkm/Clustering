package com.github.kdm1jkm.clustering.node;

import com.github.kdm1jkm.clustering.doc.Doc;

import java.util.List;

public interface Node {
    List<Doc> getDocs();

    List<Node> getChildNode();
}
