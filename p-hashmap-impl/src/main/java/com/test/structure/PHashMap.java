package com.test.structure;

import java.util.ArrayList;

public class PHashMap<K, V> {

    // Initial size oh hash table
    private static final int INITIAL_CAPACITY = 16;
    // When load of buckets list will be grater or equal to specified LOAD_FACTOR, resizing process will occur
    private static final double LOAD_FACTOR = 0.75;
    private ArrayList[] buckets;
    private int size;

    public PHashMap() {
        buckets = new ArrayList[INITIAL_CAPACITY];
        for (int i = 0; i < INITIAL_CAPACITY; i++) {
            buckets[i] = new ArrayList<>();
        }
        size = 0;
    }

    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        ArrayList<Entry<K, V>> bucket = buckets[getBucketIndex(key)];
        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }
        bucket.add(new Entry<>(key, value));
        size++;

        if ((double) size / buckets.length >= LOAD_FACTOR) {
            resize();
        }
    }

    public V get(K key) {
        ArrayList<Entry<K, V>> bucket = buckets[getBucketIndex(key)];
        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }
        return null;
    }

    public void remove(K key) {
        ArrayList<Entry<K, V>> bucket = buckets[getBucketIndex(key)];
        bucket.removeIf(entry -> entry.key.equals(key));
        size--;
    }

    public int size() {
        return size;
    }

    private int getBucketIndex(K key) {
        return Math.abs(key.hashCode()) % buckets.length;
    }

    private void resize() {
        ArrayList<Entry<K, V>>[] oldBuckets = buckets;
        buckets = new ArrayList[2 * oldBuckets.length];
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new ArrayList<>();
        }
        size = 0;
        for (ArrayList<Entry<K, V>> bucket : oldBuckets) {
            for (Entry<K, V> entry : bucket) {
                put(entry.key, entry.value);
            }
        }
    }

    private static class Entry<K, V> {
        K key;
        V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
