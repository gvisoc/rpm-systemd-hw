package com.gvisoc.hello;

public class StopMonitor implements StopMonitorMBean {

    private final Object semaphore;

    public StopMonitor(Object semaphore) {
        this.semaphore = semaphore;
    }

    @Override
    public void stop()
    {
        synchronized (semaphore) {
            semaphore.notify();
        }
    }
}