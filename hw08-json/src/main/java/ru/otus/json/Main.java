package ru.otus.json;

import java.awt.*;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        var otuson = Otuson.create();
        System.out.println(otuson.toJson(new AnyObject()));
    }

    static class AnyObject extends SuperObject {
        List<BigInteger> integers = Arrays.asList(BigInteger.TEN, BigInteger.ZERO);
        Map<String, Point> map = new HashMap<>() {
            {
                put("kek", new Point(1, 2));
                put("kok", new Point(3, 24));
            }
        };
        int[] l = { 1, 5, 10 };
    }

    static class SuperObject {
        String str = "fff";
        transient int ignored = 42;
    }
}
