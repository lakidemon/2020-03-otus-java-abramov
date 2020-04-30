package ru.otus.gc;

import com.sun.management.GarbageCollectionNotificationInfo;
import lombok.SneakyThrows;

import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
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
    static final int SIZE = 160_000; // 10_000 | 160_000
    static long START_TIME;
    static Stat STATS = new Stat();

    public static void main(String[] args) throws Exception {
        System.out.println("GC: " + GC);
        collectGCStats();
        var mf = ManagementFactory.getPlatformMBeanServer();
        var objectName = new ObjectName("ru.otus.gc:type=Stat");
        mf.registerMBean(STATS, objectName);

        START_TIME = System.currentTimeMillis();
        var list = new LinkedList<>();
        while (true) {
            for (var i = 0; i < 4; i++) {
                list.add(new Object[SIZE]);
            }
            Thread.sleep(100L);
            for (var i = 0; i < 2; i++) {
                list.removeFirst();
            }

            STATS.incrementIterations();
            STATS.setElapsedTime(System.currentTimeMillis() - START_TIME);
            STATS.setMemoryPercent(getHeapPercentage());
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
        LOGGER.println(String.join(",", String.valueOf(STATS.getIterations()), String.valueOf(STATS.getElapsedTime()),
                String.valueOf(STATS.getMemoryPercent()), action, String.valueOf(duration)));
    }

    private static int getHeapPercentage() {
        return (int) ((getUsedHeapSize() / (double) HEAP_SIZE) * 100D);
    }

    private static long getUsedHeapSize() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed() / 1024L / 1024L;
    }

    private static long getHeapSize() {
        return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() / 1024L / 1024L;
    }
}
