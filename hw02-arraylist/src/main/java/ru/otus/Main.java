package ru.otus;

import ru.otus.list.DIYArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        var list = new DIYArrayList<Integer>();
        var copySource = new DIYArrayList<>(Arrays.asList(0, 1, 2, 3, 4));
        var sample = IntStream.range(5, 50).boxed().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        Collections.addAll(list, sample.toArray(Integer[]::new));
        System.out.println(list.size() + " == " + sample.size());
        Collections.copy(list, copySource);
        System.out.println(list.get(4) + " == 4");
        Collections.sort(list, Comparator.comparingInt(Integer::intValue));
        System.out.println("Sorted list: " + list);
    }

}
