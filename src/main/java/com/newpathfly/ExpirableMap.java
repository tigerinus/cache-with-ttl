package com.newpathfly;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

public class ExpirableMap<K, V> implements IExpirableMap<K, V> {

    private class Expirable<T> {

        private final T _data;
        private long _expirationTime;

        public Expirable(T data, long timeMillisToLive) {
            _data = data;
            _expirationTime = System.currentTimeMillis() + timeMillisToLive;
        }

        public T get() {
            return _data;
        }

        public long getExpirationTime() {
            return _expirationTime;
        }

        public void setExpirationTime(long expirationTime) {
            _expirationTime = expirationTime;
        }
    }

    private static final int INITIAL_CAPACITY = 100;

    private final int _capacity;
    private final Map<K, Expirable<V>> _map;
    private final PriorityBlockingQueue<Expirable<K>> _queue;

    public ExpirableMap(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity should be large than 0.");
        }

        _capacity = capacity;

        int initialCapacity = Math.min(INITIAL_CAPACITY, _capacity);
        _map = new HashMap<>(initialCapacity);
        _queue = new PriorityBlockingQueue<>(initialCapacity,
                (o1, o2) -> Long.compare(o1.getExpirationTime(), o2.getExpirationTime()));
    }

    public V put(K key, V value, long ttl) {
        if (null == key) {
            throw new NullPointerException("key should not be null");
        }

        if (ttl <= 0) {
            throw new IllegalArgumentException("ttl should be large than 0.");
        }

        synchronized (this) {

            // if map does not contain the key user want to add,
            if (!_map.containsKey(key)) {

                // then make some room for the new key/value pair if necessary
                while (_map.size() >= _capacity) {

                    Expirable<K> oldestKey = _queue.poll();

                    if (null == oldestKey) {
                        throw new UnsupportedOperationException("this should never happen.");
                    }

                    // get the value of oldest key
                    Expirable<V> currentValue = _map.get(oldestKey.get());

                    // If value has a longer expiration time, which could happen if value is updated
                    // after first put().
                    if (currentValue.getExpirationTime() > oldestKey.getExpirationTime()) {
                        // ... then add its key back to the queue with updated expiration time,
                        oldestKey.setExpirationTime(currentValue.getExpirationTime());
                        _queue.put(oldestKey);

                        // ... keep the value and continue.
                        continue;
                    }

                    // otherwise, remove the value with oldest key from the map.
                    remove(oldestKey.get());
                }
            }

            Expirable<V> previousValue = _map.put(key, new Expirable<>(value, ttl));

            _queue.add(new Expirable<>(key, ttl));

            return null == previousValue ? null : previousValue.get();
        }
    }

    public boolean containsKey(K key) {
        Expirable<V> value = _map.get(key);

        if (null == value)
            return false;

        if (value.getExpirationTime() <= System.currentTimeMillis()) {
            remove(key);
            return false;
        }

        return true;
    }

    public V get(K key) {
        Expirable<V> value = _map.get(key);

        if (null == value)
            return null;

        if (value.getExpirationTime() <= System.currentTimeMillis()) {
            remove(key);
            return null;
        }

        return value.get();
    }

    public V remove(K key) {
        Expirable<V> value = _map.remove(key);
        return null == value ? null : value.get();
    }

    public void clear() {
        synchronized (this) {
            _map.clear();
            _queue.clear();
        }
    }
}
