package com.test.structure

import org.apache.commons.collections4.list.TreeList
import spock.lang.Specification

class PerformanceTest extends Specification {

    /* WARNING
   Results depends on your local machine & could be different in every run, but can easy show you tendency
   if you will repeat them multiple times
    */

    def 'compare put process performance'() {
        given:
        def inputNumbers = 10_000
        // remember that gap for warm up is equal 50 so, inputNumbers should be much more
        def numberOfTries = 500
        def randomKeys = generateRandomKeys(inputNumbers)

        when:
        def hashMapAvgDuration = measureDuration(() -> getHashMapWithGivenInputsNumber(inputNumbers, randomKeys),
                numberOfTries)
        def pHashMapAvgDuration = measureDuration(() -> getPHashMapWithGivenInputsNumber(inputNumbers, randomKeys),
                numberOfTries)

        then:
        println "Putting: " + inputNumbers + " inputs to standard HashMap has taken: " + hashMapAvgDuration + " nanoseconds"
        println "Putting: " + inputNumbers + " inputs to custom PHashMap has taken: " + pHashMapAvgDuration + " nanoseconds"
        println "Is PHashMap faster for put process? " + (pHashMapAvgDuration < hashMapAvgDuration)
        println "Difference [milliseconds]: " + (pHashMapAvgDuration - hashMapAvgDuration) / 1_000_000
    }

    def 'compare get process performance'() {
        given:
        def inputNumbers = 10_000
        // remember that gap for warm up is equal 50 so, inputNumbers should be much more
        def numberOfTries = 500
        def randomKeys = generateRandomKeys(inputNumbers)
        def hashMap = getHashMapWithGivenInputsNumber(inputNumbers, randomKeys)
        def pHashMap = getPHashMapWithGivenInputsNumber(inputNumbers, randomKeys)

        when:
        def hashMapAvgDuration = measureDuration(() -> getAllFromHashMapOneByOne(hashMap, randomKeys),
                numberOfTries)
        def pHashMapAvgDuration = measureDuration(() -> getAllFromPHashMapOneByOne(pHashMap, randomKeys),
                numberOfTries)
        def hashMapResult = getAllFromHashMapOneByOne(hashMap, randomKeys)
        def pHashMapResult = getAllFromPHashMapOneByOne(pHashMap, randomKeys)

        then:
        hashMap.size() == hashMapResult
        pHashMap.size() == pHashMapResult

        println "Getting: " + inputNumbers + " inputs from standard HashMap has taken: " + hashMapAvgDuration + " nanoseconds"
        println "Getting: " + inputNumbers + " inputs from custom PHashMap has taken: " + pHashMapAvgDuration + " nanoseconds"
        println "Is PHashMap faster for get process? " + (pHashMapAvgDuration < hashMapAvgDuration)
        println "Difference [milliseconds]: " + (pHashMapAvgDuration - hashMapAvgDuration) / 1_000_000
    }

    def 'compare remove process performance'() {
        given:
        def inputNumbers = 10_000
        // remember that gap for warm up is equal 50 so, inputNumbers should be much more
        def numberOfTries = 500
        def randomKeys = generateRandomKeys(inputNumbers)
        def hashMap = getHashMapWithGivenInputsNumber(inputNumbers, randomKeys)
        def pHashMap = getPHashMapWithGivenInputsNumber(inputNumbers, randomKeys)

        when:
        def hashMapAvgDuration = measureHashMapRemoveDuration(hashMap, randomKeys, numberOfTries)
        def pHashMapAvgDuration = measurePHashMapRemoveDuration(pHashMap, randomKeys, numberOfTries)
        def hashMapResult = removeAllFromHashMapOneByOne(hashMap, randomKeys)
        def pHashMapResult = removeAllFromPHashMapOneByOne(pHashMap, randomKeys)

        then:
        hashMap.size() == hashMapResult
        pHashMap.size() == pHashMapResult

        println "Removing: " + inputNumbers + " inputs from standard HashMap has taken: " + hashMapAvgDuration + " nanoseconds"
        println "Removing: " + inputNumbers + " inputs from custom PHashMap has taken: " + pHashMapAvgDuration + " nanoseconds"
        println "Is PHashMap faster for remove process? " + (pHashMapAvgDuration < hashMapAvgDuration)
        println "Difference [milliseconds]: " + (pHashMapAvgDuration - hashMapAvgDuration) / 1_000_000
    }

    private static int measurePHashMapRemoveDuration(PHashMap<String, UUID> pHashMap, List<String> randomKeys, int numberOfTries) {
        long totalTime = 0
        int gapForWarmUp = 50
        for (i in 0..<numberOfTries) {
            PHashMap<String, UUID> pHashMapCopy = new PHashMap<>()
            randomKeys.forEach { it -> pHashMapCopy.put(it, pHashMap.get(it)) }
            long startTime = System.nanoTime();
            removeAllFromPHashMapOneByOne(pHashMapCopy, randomKeys)
            long endTime = System.nanoTime();
            if (i >= gapForWarmUp) {
                totalTime += (endTime - startTime);
            }
        }
        return totalTime / (numberOfTries - gapForWarmUp)
    }

    private static int measureHashMapRemoveDuration(HashMap<String, UUID> hashMap, List<String> randomKeys, int numberOfTries) {
        long totalTime = 0
        int gapForWarmUp = 50
        for (i in 0..<numberOfTries) {
            HashMap<String, UUID> hashMapCopy = new HashMap<>()
            randomKeys.forEach { it -> hashMapCopy.put(it, hashMap.get(it)) }
            long startTime = System.nanoTime();
            removeAllFromHashMapOneByOne(hashMapCopy, randomKeys)
            long endTime = System.nanoTime();
            if (i >= gapForWarmUp) {
                totalTime += (endTime - startTime);
            }
        }
        return totalTime / (numberOfTries - gapForWarmUp)
    }

    private static int measureDuration(Runnable runnable, int numberOfTries) {
        long totalTime = 0
        int gapForWarmUp = 50
        for (i in 0..<numberOfTries) {
            long startTime = System.nanoTime();
            runnable.run()
            long endTime = System.nanoTime();
            if (i >= gapForWarmUp) {
                totalTime += (endTime - startTime);
            }
        }
        return totalTime / (numberOfTries - gapForWarmUp)
    }

    private static List<String> generateRandomKeys(int numberOfKeys) {
        List<String> randomKeys = new TreeList<>()
        for (i in 0..<numberOfKeys) {
            randomKeys.add(UUID.randomUUID().toString())
        }
        return randomKeys
    }

    private static int removeAllFromPHashMapOneByOne(PHashMap<String, UUID> pHashMap, List<String> randomKeys) {
        int i = randomKeys.size()
        randomKeys.forEach { it -> { pHashMap.remove(it); i-- } }
        return i;
    }

    private static int removeAllFromHashMapOneByOne(HashMap<String, UUID> hashMap, List<String> randomKeys) {
        int i = randomKeys.size()
        randomKeys.forEach { it -> { hashMap.remove(it); i-- } }
        return i;
    }

    private static int getAllFromPHashMapOneByOne(PHashMap<String, UUID> pHashMap, List<String> randomKeys) {
        int i = 0
        randomKeys.forEach { it -> { pHashMap.get(it); i++ } }
        return i;
    }

    private static int getAllFromHashMapOneByOne(HashMap<String, UUID> hashMap, List<String> randomKeys) {
        int i = 0
        randomKeys.forEach { it -> { hashMap.get(it); i++ } }
        return i;
    }

    private static PHashMap<String, UUID> getPHashMapWithGivenInputsNumber(int inputsNumber, List<String> randomKeys) {
        PHashMap<String, UUID> pHashMap = new PHashMap<>()

        for (i in 0..<inputsNumber) {
            pHashMap.put(randomKeys.get(i), UUID.randomUUID())
        }
        return pHashMap
    }

    private static HashMap<String, UUID> getHashMapWithGivenInputsNumber(int inputsNumber, List<String> randomKeys) {
        HashMap<String, UUID> hashMap = new HashMap<>()

        for (i in 0..<inputsNumber) {
            hashMap.put(randomKeys.get(i), UUID.randomUUID())
        }
        return hashMap
    }
}
