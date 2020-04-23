package ru.otus.testing;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TestResult {
    private String testName;
    private long runningTimeInMillis;
    private Throwable failure;

    public boolean isSucceed() {
        return failure == null;
    }
}
