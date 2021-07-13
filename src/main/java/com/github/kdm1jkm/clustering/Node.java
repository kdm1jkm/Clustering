package com.github.kdm1jkm.clustering;

import java.util.List;

public interface Node {
    List<Doc> getDocs();
    List<Node> getChildNode();
}
