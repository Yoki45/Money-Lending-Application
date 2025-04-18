package com.lms.utils;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static final String STATUS_201 = "201";
    public static final String STATUS_200 = "200";
    public static final String STATUS_417 = "417";
    public static final int MAX_PAGE_SIZE = 50;




    public static <T> List<List<T>> createSubList(List<T> list, int subListSize) {
        List<List<T>> listOfSubList = new ArrayList<>();
        for (int i = 0; i < list.size(); i += subListSize) {
            if (i + subListSize <= list.size()) {
                listOfSubList.add(list.subList(i, i + subListSize));
            } else {
                listOfSubList.add(list.subList(i, list.size()));
            }
        }
        return listOfSubList;
    }

    public static boolean stringNullOrEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }




}

