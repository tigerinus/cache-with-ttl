package com.tensorsmart;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

public class ExpirableMap<K, V> {

    private final static int INITIAL_CAPACITY = 100;

    private final int _capacity;
    private final Map<K, ExpirableValue<V>> _map;
    private final PriorityBlockingQueue<ExpirableKey<K>> _queue;

    public ExpirableMap(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity should be large than 0.");
        }

        _capacity = capacity;

        int initialCapacity = Math.min(INITIAL_CAPACITY, _capacity);
        _map = new HashMap<>(initialCapacity);
        _queue = new PriorityBlockingQueue<>(initialCapacity, new Comparator<ExpirableKey<K>>() {

            @Override
            public int compare(ExpirableKey<K> o1, ExpirableKey<K> o2) {
                return o1.getExpirationTime().compareTo(o2.getExpirationTime());
            }
        });
    }

    public boolean containsKey(K key) {
        ExpirableValue<V> value = _map.get(key);

        if (null == value)
            return false;

        if (value.getExpirationTime() <= System.currentTimeMillis()) {
            remove(key);
            return false;
        }

        return true;
    }

    public V get(K key) {
        ExpirableValue<V> value = _map.get(key);

        if (null == value)
            return null;

        if (value.getExpirationTime() <= System.currentTimeMillis()) {
            remove(key);
            return null;
        }

        return value.get();
    }

    public V put(K key, V value, long timeMillisToLive) {
        if (null == key) {
            throw new NullPointerException("key should not be null");
        }

        if (timeMillisToLive <= 0) {
            throw new IllegalArgumentException("timeMillisToLive should be large than 0.");
        }

        synchronized (this) {

            if (!_map.containsKey(key)) {
                while (_map.size() >= _capacity) {
                    ExpirableKey<K> oldestKey = _queue.poll();

                    if (null == oldestKey) {
                        throw new RuntimeException("this should never happen.");
                    }

                    ExpirableValue<V> currentValue = _map.get(oldestKey.get());

                    if (currentValue.getExpirationTime() > oldestKey.getExpirationTime())
                        continue;

                    remove(oldestKey.get());
                }
            }

            ExpirableValue<V> previousValue = _map.put(key, new ExpirableValue<V>(value, timeMillisToLive));

            _queue.add(new ExpirableKey<K>(key, timeMillisToLive));

            return null == previousValue ? null : previousValue.get();
        }
    }

    public V remove(K key) {
        ExpirableValue<V> value = _map.remove(key);
        return null == value ? null : value.get();
    }

    public void clear() {
        synchronized (this) {
            _map.clear();
            _queue.clear();
        }
    }
}
