package ru.otus.cachehw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.model.Phone;

/**
 * @author sergey
 * created on 14.12.18.
 */
public class HWCacheDemo {
    private static final Logger logger = LoggerFactory.getLogger(HWCacheDemo.class);

    public static void main(String[] args) throws InterruptedException {
        new HWCacheDemo().demo2();
    }

    private void demo2() throws InterruptedException {
        var cache = new MyCache<String, Phone>();
        cache.addListener((key, value, action) -> System.out.println(
                String.format("K: %s, V: %s, A: %s", key, String.valueOf(value), action)));

        cache.put("key1", new Phone("88005553535"));
        cache.put("key2", new Phone("+79130846781"));
        cache.put("key3", new Phone("88001002772"));

        System.gc();
        Thread.sleep(1000L);

        assert cache.get("key1") == null; // в логе будет соответствующее подтверждение от листенера
    }
}
