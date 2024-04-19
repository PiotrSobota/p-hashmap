package com.test.structure

import spock.lang.Specification

class PHashMapTest extends Specification {

    /* I know it's not good practice to use private fields [to say more it's very bad practice :)] in unit tests of course,
        but it's special situation where we test some 'basic' class for other classes which have to be fast, so it
        cannot be separated so much using OOP like in classic business code (using a lot of mocks in units etc.)
     */

    def 'should get value'() {
        given:
        PHashMap<String, String> testMap = new PHashMap<>()
        def key1 = 'key1'
        def key2 = 'key2'
        def key1BucketNumber = key1.hashCode() % testMap.INITIAL_CAPACITY
        def key2BucketNumber = key2.hashCode() % testMap.INITIAL_CAPACITY
        testMap.buckets[key1BucketNumber].add(new PHashMap.Entry<String, String>(key1, 'value1'))
        testMap.buckets[key2BucketNumber].add(new PHashMap.Entry<String, String>(key2, 'value2'))
        testMap.size = 2

        when:
        def result1 = testMap.get(key1)
        def result2 = testMap.get(key2)

        then:
        result1 == 'value1'
        result2 == 'value2'
    }

    def 'should get correct value for 2 entries with same hashcode of key'() {
        given:
        PHashMap<String, String> testMap = new PHashMap<>()
        def key1 = 'Ea'
        def key2 = 'FB'
        def key1BucketNumber = key1.hashCode() % testMap.INITIAL_CAPACITY
        def key2BucketNumber = key2.hashCode() % testMap.INITIAL_CAPACITY
        testMap.buckets[key1BucketNumber].add(new PHashMap.Entry<String, String>(key1, 'value1'))
        testMap.buckets[key2BucketNumber].add(new PHashMap.Entry<String, String>(key2, 'value2'))
        testMap.size = 2

        when:
        def result1 = testMap.get(key1)
        def result2 = testMap.get(key2)

        then:
        key1.hashCode() == key2.hashCode()
        result1 == 'value1'
        result2 == 'value2'
    }

    def 'should put new items to map & overwrite if exists'() {
        given:
        PHashMap<String, String> testMap = new PHashMap<>()
        def key1 = 'key1'
        def key2 = 'key2'
        def key1BucketNumber = key1.hashCode() % testMap.INITIAL_CAPACITY
        def key2BucketNumber = key2.hashCode() % testMap.INITIAL_CAPACITY

        when:
        testMap.put(key1, 'value1')
        testMap.put(key2, 'value2')
        testMap.put(key1, 'value3')

        then:
        testMap.size == 2
        testMap.buckets[key1BucketNumber].get(0).value == 'value3'
        testMap.buckets[key2BucketNumber].get(0).value == 'value2'
    }

    def 'should put new item to map even if hashcode of key is the same'() {
        given:
        PHashMap<String, String> testMap = new PHashMap<>()
        def key1 = 'Ea'
        def key2 = 'FB'
        def key1BucketNumber = key1.hashCode() % testMap.INITIAL_CAPACITY
        def key2BucketNumber = key2.hashCode() % testMap.INITIAL_CAPACITY

        when:
        testMap.put(key1, 'value1')
        testMap.put(key2, 'value2')

        then:
        key1.hashCode() == key2.hashCode()
        testMap.size == 2
        testMap.buckets[key1BucketNumber].get(0).value == 'value1'
        testMap.buckets[key2BucketNumber].get(1).value == 'value2'
    }

    def 'should remove item from map'() {
        given:
        PHashMap<String, String> testMap = new PHashMap<>()
        def key1 = 'key1'
        def key2 = 'key2'
        def key3 = 'key3'
        def key1BucketNumber = key1.hashCode() % testMap.INITIAL_CAPACITY
        def key2BucketNumber = key2.hashCode() % testMap.INITIAL_CAPACITY
        def key3BucketNumber = key3.hashCode() % testMap.INITIAL_CAPACITY
        testMap.buckets[key1BucketNumber].add(new PHashMap.Entry<String, String>('key1', 'value1'))
        testMap.buckets[key2BucketNumber].add(new PHashMap.Entry<String, String>('key2', 'value2'))
        testMap.buckets[key3BucketNumber].add(new PHashMap.Entry<String, String>('key3', 'value3'))
        testMap.size = 3

        when:
        testMap.remove('key2')

        then:
        testMap.size == 2
        testMap.buckets[key1BucketNumber].get(0).value == 'value1'
        testMap.buckets[key3BucketNumber].get(0).value == 'value3'
    }
}
