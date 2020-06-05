package ru.otus.json;

import lombok.EqualsAndHashCode;

import java.awt.*;
import java.math.BigInteger;
import java.time.Year;
import java.util.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
class SomeObject extends SuperObject {
    private Map<String, Integer> map = new HashMap<>() {{
        put("key1", 101);
        put("key2", 202);
    }};
    private List<Year> yearList = Arrays.asList(Year.now(), Year.of(2007));
    private Set<Object> emptySet = new HashSet<>();
    private String nullField = null;
}

@EqualsAndHashCode
class SuperObject {
    private int primitiveInt = 100;
    private Integer boxedInteger = 200;
    private float primitiveFloat = 1.01f;
    private double primitiveDouble = 2.02d;
    private String string = "string";
    private BigInteger bigInteger = BigInteger.ZERO;
    private boolean[] primitiveBooleanArray = { true, false, true };
    private boolean[] boxedBooleanArray = { true, false, true };
    private Point complexObject = new Point(1920, 1080);
    private Point[] objectArray = { new Point(0, 0), new Point(1280, 720) };
    private transient String ignoredTransient = "transient";
    private static String ignoredStatic = "static";
}