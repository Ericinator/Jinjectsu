package com.ericlouw.jinjectsu.jinjectsu.utils;

import java.util.List;

public class ListUtilWrapper<T> {
    private List<T> list;

    public ListUtilWrapper(List<T> list){
        this.list = list;
    }

    public String toDelimitedString(String delimiter){
        String separator = "";
        StringBuilder sb = new StringBuilder("");

        for (T item : this.list){
            sb.append(separator).append(item.toString());
            separator = delimiter;
        }

        return sb.toString();
    }
}
