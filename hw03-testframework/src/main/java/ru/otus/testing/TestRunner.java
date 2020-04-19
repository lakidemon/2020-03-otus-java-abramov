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
                    System.out.println();
                } else {
                    System.out.print("\r   [F] " + result.getTestName());
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
        System.out.println("   Passed: " + pass);
        System.out.println("   Failed: " + fail);
        System.out.println("   Total: " + result.size());
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
