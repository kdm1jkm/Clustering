package com.github.kdm1jkm.clustering;

import java.util.Collections;
import java.util.List;

public class DocNode implements Node {
    private final Doc doc;

    public DocNode(Doc doc) {
        this.doc = doc;
    }

    @Override
    public List<Doc> getDocs() {
        return Collections.singletonList(doc);
    }

    @Override
    public List<Node> getChildNode() {
        return Collections.emptyList();
    }
}
