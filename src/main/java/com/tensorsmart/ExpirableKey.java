package com.tensorsmart;

public class ExpirableKey<K> {

    private final K _key;
    private final long _expirationTime;

    public ExpirableKey(K key, long TimeMillisToLive) {
        _key = key;

        _expirationTime = System.currentTimeMillis() + TimeMillisToLive;
    }

    public K get() {
        return _key;
    }

    public Long getExpirationTime() {
        return _expirationTime;
    }
}
