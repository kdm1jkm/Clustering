package com.github.kdm1jkm.clustering;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        for (String keyWord : new String[]{"밥", "나무위키", "에러"}) {
            NamuDoc namuDoc = new NamuDoc(keyWord);
            System.out.println(namuDoc.getContent());
        }
    }
}
