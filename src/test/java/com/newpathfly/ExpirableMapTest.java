package com.newpathfly;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ExpirableMapTest {

    @Test
    void positiveTest1() throws InterruptedException {

        ExpirableMap<String, String> map = new ExpirableMap<>(1);

        long ttl = 1000;

        map.put("Foo", "Bar", ttl);

        String result;

        result = map.get("Foo");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Bar", result);

        Thread.sleep(ttl);

        result = map.get("Foo");
        Assertions.assertNull(result);
    }

    @Test
    void positiveTest2() throws InterruptedException {
        ExpirableMap<String, String> map = new ExpirableMap<>(1);

        long ttl = 1000;

        map.put("Foo", "Bar", ttl);

        String result;

        result = map.get("Foo");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Bar", result);

        map.put("Foo", "Bar", ttl * 2);

        Thread.sleep(ttl);

        result = map.get("Foo");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Bar", result);

        Thread.sleep(ttl);

        result = map.get("Foo");
        Assertions.assertNull(result);
    }

    @Test
    void positiveTest3() throws InterruptedException {
        ExpirableMap<String, String> map = new ExpirableMap<>(1);

        long ttl = 1000;

        map.put("Foo1", "Bar", ttl);

        String result;

        result = map.get("Foo1");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Bar", result);

        map.put("Foo2", "Bar", ttl);

        result = map.get("Foo1");
        Assertions.assertNull(result);

        result = map.get("Foo2");
        Assertions.assertNotNull(result);
        Assertions.assertEquals("Bar", result);

        Thread.sleep(ttl);

        result = map.get("Foo1");
        Assertions.assertNull(result);

        result = map.get("Foo2");
        Assertions.assertNull(result);
    }

    @Test
    void positiveTest4() throws InterruptedException {
        ExpirableMap<String, String> map = new ExpirableMap<>(3);

        long ttl = 1000;

        for (String key : new String[] { "Foo1", "Foo2", "Foo3" }) {
            map.put(key, "Bar", ttl);
            Thread.sleep(1);
        }

        String result;
        for (String key : new String[] { "Foo1", "Foo2", "Foo3" }) {
            result = map.get(key);

            Assertions.assertNotNull(result);
            Assertions.assertEquals("Bar", result);
        }

        map.put("Foo4", "Bar", ttl);

        result = map.get("Foo1");
        Assertions.assertNull(result);

        for (String key : new String[] { "Foo2", "Foo3", "Foo4" }) {
            result = map.get(key);

            Assertions.assertNotNull(result);
            Assertions.assertEquals("Bar", result);
        }

        Thread.sleep(ttl);

        for (String key : new String[] { "Foo1", "Foo2", "Foo3", "Foo4" }) {
            result = map.get(key);

            Assertions.assertNull(result);
        }
    }

    @Test
    void positiveTest5() {
        ExpirableMap<String, String> map = new ExpirableMap<>(3);

        long ttl = 1000;

        for (String key : new String[] { "Foo1", "Foo2", "Foo3" }) {
            map.put(key, "Bar", ttl);
        }

        String result;
        for (String key : new String[] { "Foo1", "Foo2", "Foo3" }) {
            result = map.get(key);

            Assertions.assertNotNull(result);
            Assertions.assertEquals("Bar", result);
        }

        map.clear();

        for (String key : new String[] { "Foo1", "Foo2", "Foo3" }) {
            result = map.get(key);

            Assertions.assertNull(result);
        }
    }

    @Test
    void positiveTest6() throws InterruptedException {
        ExpirableMap<String, String> map = new ExpirableMap<>(2);

        long ttl = 1000;

        map.put("Foo1", "Bar", ttl);

        Thread.sleep(1);

        map.put("Foo2", "Bar", ttl);

        Thread.sleep(1);

        map.put("Foo1", "Bar", ttl * 2);

        Thread.sleep(ttl);

        Assertions.assertTrue(map.containsKey("Foo1"));
        Assertions.assertFalse(map.containsKey("Foo2"));
    }

    @Test
    void positiveTest7() throws InterruptedException {
        ExpirableMap<String, String> map = new ExpirableMap<>(2);

        long ttl = 1000;

        map.put("Foo1", "Bar", ttl);

        Thread.sleep(1);

        map.put("Foo2", "Bar", ttl);

        Thread.sleep(1);

        map.put("Foo1", "Bar", ttl * 2);

        Thread.sleep(1);

        map.put("Foo3", "Bar", ttl * 2);

        Thread.sleep(ttl);

        Assertions.assertTrue(map.containsKey("Foo1"));
        Assertions.assertFalse(map.containsKey("Foo2"));
        Assertions.assertTrue(map.containsKey("Foo3"));
    }

    @Test
    void positiveTest8() {
        ExpirableMap<String, String> map = new ExpirableMap<>(2);

        Assertions.assertNull(map.remove("Foo"));
    }

    @Test
    void negativeTest1() {
        Assertions.assertThrows(IllegalArgumentException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                new ExpirableMap<>(0);
            }
        });
    }

    @Test
    void negativeTest2() {
        Assertions.assertThrows(NullPointerException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                new ExpirableMap<>(1).put(null, "bar", 1000);
            }
        });
    }

    @Test
    void negativeTest3() {
        Assertions.assertThrows(IllegalArgumentException.class, new Executable() {

            @Override
            public void execute() throws Throwable {
                new ExpirableMap<>(1).put("Foo", "bar", 0);
            }
        });
    }
}
