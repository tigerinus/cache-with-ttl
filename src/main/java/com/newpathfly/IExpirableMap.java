package com.newpathfly;

public interface IExpirableMap<K, V> {

    boolean containsKey(K key);

    V get(K key);
    V put(K key, V value, long ttl);

    V remove(K key);

    void clear();
}
