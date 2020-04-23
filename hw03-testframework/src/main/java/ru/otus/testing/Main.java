package ru.otus.testing;

public class Main {

    public static void main(String[] args) throws Exception {
        TestRunner.runTests(args.length == 0 ? ExampleTest.class.getName() : args[0]);
    }

}
