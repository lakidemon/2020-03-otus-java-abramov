package ru.otus;

import com.google.common.base.Joiner;

public class HelloOtus {

    public static void main(String[] args) {
        System.out.println(Joiner.on(' ').join("Hello", "Otus"));
    }

}
