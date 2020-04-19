package ru.otus.testing;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestRunner {

    public static void runTests(String className) throws ClassNotFoundException {
        var clazz = Class.forName(className);

        var container = new TestsContainer(clazz);
        container.collectTests();

        System.out.println("Running " + clazz.getSimpleName());
        var result = container.startTests(new TestsContainer.TestProgress() {
            @Override
            public void onTestBegin(TestCase test) {
                System.out.print("   [R] " + test.getTestName());
            }

            @Override
            public void onTestFinish(TestResult result) {
                if (result.isSucceed()) {
                    System.out.print("\r   [P] " + result.getTestName());
                    System.out.print(" " + result.getRunningTime() + "ms.");
                    System.out.println();
                } else {
                    System.out.print("\r   [F] " + result.getTestName());
                    System.out.print(" " + result.getRunningTime() + "ms.");
                    System.out.println();
                    result.getFailure()
                            .printStackTrace(IndentationPrintStream.ERR); // при запуске из идеи коряво отображается
                }
            }
        });

        int pass = 0, fail = 0;
        for (TestResult testResult : result) {
            if (testResult.isSucceed()) {
                pass++;
            } else {
                fail++;
            }
        }

        System.out.println("Complete:");
        System.out.println("   Passed: " + pass + " (" + percentage(pass, result.size()) + ")");
        System.out.println("   Failed: " + fail + " (" + percentage(fail, result.size()) + ")");
        System.out.println("   Total: " + result.size());
    }

    private static String percentage(double amount, double total) {
        return ((int) ((amount / total) * 100D)) + "%";
    }

    private static class IndentationPrintStream extends PrintStream {
        static IndentationPrintStream ERR = new IndentationPrintStream(System.err);
        private final PrintStream delegate;

        public IndentationPrintStream(PrintStream delegate) {
            super(new ByteArrayOutputStream());
            this.delegate = delegate;
        }

        @Override
        public void println(Object object) {
            delegate.println((Object) ("   " + object));
        }
    }
}
