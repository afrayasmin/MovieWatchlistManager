package com.movieapp.database;

/**
 * Tracks how many database operations are currently running across all threads.
 * This is a genuinely shared mutable resource: the JavaFX Application Thread and
 * one or more background worker threads can all call these methods at the same
 * time, so every access is synchronized to prevent race conditions on the counter.
 */
public class DatabaseActivityMonitor {

    private static int activeOperations = 0;

    public static synchronized void operationStarted() {
        activeOperations++;
        System.out.println("[DB Monitor] Operation started. Active: " + activeOperations
                + " (thread: " + Thread.currentThread().getName() + ")");
    }

    public static synchronized void operationFinished() {
        activeOperations--;
        System.out.println("[DB Monitor] Operation finished. Active: " + activeOperations
                + " (thread: " + Thread.currentThread().getName() + ")");
    }

    public static synchronized int getActiveOperations() {
        return activeOperations;
    }
}