package ru.otus;

import lombok.experimental.Delegate;
import org.junit.jupiter.api.*;

import java.io.PrintStream;
import java.io.StringWriter;

class SynchronizedNumbersTest {
    private PrintStream defaultOut;
    private TestingPrintStream testingOut;

    private static String compare;

    @BeforeAll
    static void createReference() {
        var builder = new StringBuilder();
        for (int i = 0; i < SynchronizedNumbers.cycles; i++) {
            var number = 0;
            for (int c = 0; c < 20; c++) {
                if (c < 10)
                    number++;
                else
                    number--;

                builder.append("T1: ").append(number).append('\n').append("T2: ").append(number).append('\n');
            }
        }
        compare = builder.toString();
    }

    @BeforeEach
    void setUp() {
        defaultOut = System.out;
        System.setOut(testingOut = new TestingPrintStream(defaultOut, new StringWriter()));
    }

    @RepeatedTest(1_000)
    void shouldPrintNumbersSequentially() {
        new SynchronizedNumbers().start();

        Assertions.assertEquals(compare, testingOut.getOutput());
    }

    @AfterEach
    void tearDown() {
        System.setOut(defaultOut);
    }

    public static class TestingPrintStream extends PrintStream {
        @Delegate(excludes = Ign.class)
        private PrintStream delegate;
        private StringWriter writer;

        public TestingPrintStream(PrintStream delegate, StringWriter writer) {
            super(nullOutputStream());
            this.delegate = delegate;
            this.writer = writer;
        }

        @Override
        public void println(String x) {
            super.println(x);
            writer.write(x);
            writer.append('\n');
        }

        public String getOutput() {
            return writer.toString();
        }

        private interface Ign {
            void println(String object);
        }
    }
}