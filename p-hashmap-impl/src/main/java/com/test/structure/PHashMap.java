package com.test.structure;

import org.apache.commons.collections4.list.TreeList;

public class PHashMap<K, V> {

    // Initial size oh hash table
    private static final int INITIAL_CAPACITY = 16;
    // When load of buckets list will be grater or equal to specified LOAD_FACTOR, resizing process will occur
    private static final double LOAD_FACTOR = 75;
    private TreeList<Entry<K, V>>[] buckets;
    private int size;

    public PHashMap() {
        this.buckets = new TreeList[INITIAL_CAPACITY];
        for (int i = 0; i < INITIAL_CAPACITY; i++) {
            this.buckets[i] = new TreeList<>();
        }
        this.size = 0;
    }

    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        TreeList<Entry<K, V>> bucket = this.buckets[getBucketIndex(key)];
        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
        }
        bucket.add(new Entry<>(key, value));
        this.size++;

        if ((double) this.size / this.buckets.length >= LOAD_FACTOR) {
            resize();
        }
    }

    public V get(K key) {
        TreeList<Entry<K, V>> bucket = this.buckets[getBucketIndex(key)];
        for (Entry<K, V> entry : bucket) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
        }
        return null;
    }

    public void remove(K key) {
        TreeList<Entry<K, V>> bucket = this.buckets[getBucketIndex(key)];
        bucket.removeIf(entry -> entry.key.equals(key));
        this.size--;
    }

    public int size() {
        return size;
    }

    private int getBucketIndex(K key) {
        return Math.abs(key.hashCode()) % this.buckets.length;
    }

    private void resize() {
        TreeList<Entry<K, V>>[] oldBuckets = this.buckets;
        this.buckets = new TreeList[2 * oldBuckets.length];
        for (int i = 0; i < this.buckets.length; i++) {
            this.buckets[i] = new TreeList<>();
        }
        this.size = 0;
        for (TreeList<Entry<K, V>> bucket : oldBuckets) {
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
