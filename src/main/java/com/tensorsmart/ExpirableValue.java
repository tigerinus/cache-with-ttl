package com.tensorsmart;

public class ExpirableValue<V> {
    
    private final V _value;
    private final long _expirationTime;

    public ExpirableValue(V value, long TimeMillisToLive) {
        _value = value;

        _expirationTime = System.currentTimeMillis() + TimeMillisToLive;
    }

    public V get() {
        return _value;
    }

    public Long getExpirationTime() {
        return _expirationTime;
    }

}
