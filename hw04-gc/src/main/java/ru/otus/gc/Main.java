package ru.otus.gc;

import com.sun.management.GarbageCollectionNotificationInfo;
import lombok.SneakyThrows;

import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.LinkedList;

public class Main {
    static final String GC = getGCName();
    static final long HEAP_SIZE = getHeapSize();
    static final PrintStream LOGGER = !GC.equals("Epsilon") ? createOutput(GC + "-" + HEAP_SIZE + "MB") : null;
    static final int AMOUNT = 29_000; // 1_600 | 7_000 | 29_000

    // Epsilon:
    // 296 | 306 | 305

    public static void main(String[] args) throws Exception {
        System.out.println("GC: " + GC);
        collectGCStats();
        var list = new LinkedList<>();
        while (true) {
            for (var i = 0; i < AMOUNT; i++) {
                list.add(new Long(i));
            }
            Thread.sleep(100L);
            for (var i = 0; i < AMOUNT / 2; i++) {
                list.removeFirst();
            }
        }
    }

    @SneakyThrows
    private static PrintStream createOutput(String fileName) {
        return new PrintStream(
                new FileOutputStream(String.join(File.separator, ".", "hw04-gc", "stats", fileName + ".csv"), true),
                true);
    }

    private static void collectGCStats() {
        if (!GC.equals("Epsilon")) {
            var gcbeans = java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();
            for (var gcbean : gcbeans) {
                var emitter = (NotificationEmitter) gcbean;
                NotificationListener listener = (notification, handback) -> {
                    if (notification.getType()
                            .equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
                        var info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
                        var gcAction = info.getGcAction();
                        var gcCause = info.getGcCause();
                        var duration = info.getGcInfo().getDuration();

                        if (!gcCause.equals("System.gc()"))
                            log(gcAction, duration);
                    }
                };
                emitter.addNotificationListener(listener, null, null);
            }
        }
    }

    private static String getGCName() {
        return ManagementFactory.getRuntimeMXBean()
                .getInputArguments()
                .stream()
                .filter(s -> s.endsWith("GC"))
                .findFirst()
                .map(s -> s.substring(8))
                .map(s -> s.substring(0, s.length() - 2))
                .get();
    }

    @SneakyThrows
    private static void log(String action, long duration) {
        LOGGER.println(String.join(",", action, String.valueOf(duration)));
    }

    private static long getHeapSize() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1024 / 1024;
    }
}
