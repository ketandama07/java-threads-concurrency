package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * These exercises cover the fundamentals of Java threading.
 * Learn how to create, start, and control threads, as well as
 * handle basic thread synchronization.
 * <p>
 * Read first:
 * - https://docs.oracle.com/javase/tutorial/essential/concurrency/threads.html
 * - https://docs.oracle.com/javase/tutorial/essential/concurrency/sync.html
 */
public class ThreadBasics extends ThreadBasicsBase {

    /**
     * Create and start a thread that increments the counter.
     * Use CountDownLatch to ensure thread completion before verification.
     */
    @Test
    public void create_and_start_thread() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);

        // TODO: Create and start a thread that increments the counter
        // Thread should increment counter and count down the latch

        // Wait for thread completion
        boolean completed = latch.await(1, TimeUnit.SECONDS);
        Assertions.assertTrue(completed, "Thread did not complete in time");
        Assertions.assertEquals(1, counter.get());
    }

    /**
     * Create multiple threads that increment a shared counter.
     * Use proper synchronization to prevent race conditions.
     */
    @Test
    public void synchronized_counter() throws InterruptedException {
        Counter counter = new Counter();
        int numberOfThreads = 10;
        int incrementsPerThread = 1000;
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // TODO: Create multiple threads that increment the counter
        // TODO: Use synchronization to prevent race conditions
        // Each thread should count down the latch when done

        // Wait for all threads to complete
        boolean completed = latch.await(2, TimeUnit.SECONDS);
        Assertions.assertTrue(completed, "Threads did not complete in time");
        Assertions.assertEquals(numberOfThreads * incrementsPerThread, counter.getValue());
    }

    /**
     * Implement a producer-consumer pattern using wait() and notify().
     * The producer adds numbers to a shared queue, and the consumer processes them.
     */
    @Test
    public void producer_consumer() throws InterruptedException {
        SharedQueue<Integer> queue = new SharedQueue<>(5);
        List<Integer> consumed = new ArrayList<>();
        CountDownLatch producerLatch = new CountDownLatch(1);
        CountDownLatch consumerLatch = new CountDownLatch(1);

        // TODO: Implement producer thread that adds numbers 1-10 to queue
        // TODO: Implement consumer thread that processes numbers from queue
        // Each thread should count down its latch when done

        // Wait for both threads to complete
        boolean producerCompleted = producerLatch.await(2, TimeUnit.SECONDS);
        boolean consumerCompleted = consumerLatch.await(2, TimeUnit.SECONDS);

        Assertions.assertTrue(producerCompleted && consumerCompleted, "Threads did not complete in time");
        Assertions.assertEquals(10, consumed.size());
        for (int i = 0; i < 10; i++) {
            Assertions.assertEquals(i + 1, consumed.get(i));
        }
    }

    /**
     * Implement thread interruption handling.
     * The thread should stop processing when interrupted.
     */
    @Test
    public void handle_interruption() throws InterruptedException {
        AtomicInteger processedItems = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);

        // TODO: Create a thread that processes items until interrupted
        // TODO: Implement proper interruption handling
        // Thread should count down the latch when interrupted

        // Wait for thread completion
        boolean completed = latch.await(2, TimeUnit.SECONDS);
        Assertions.assertTrue(completed, "Thread did not handle interruption properly");
        Assertions.assertEquals(5, processedItems.get());
    }
}

class ThreadBasicsBase {
    static class Counter {
        private int count = 0;

        public synchronized void increment() {
            count++;
        }

        public synchronized int getValue() {
            return count;
        }
    }

    static class SharedQueue<T> {
        private final List<T> queue;
        private final int capacity;

        public SharedQueue(int capacity) {
            this.capacity = capacity;
            this.queue = new ArrayList<>();
        }

        public synchronized void put(T item) throws InterruptedException {
            while (queue.size() == capacity) {
                wait();
            }
            queue.add(item);
            notifyAll();
        }

        public synchronized T take() throws InterruptedException {
            while (queue.isEmpty()) {
                wait();
            }
            T item = queue.remove(0);
            notifyAll();
            return item;
        }
    }
}