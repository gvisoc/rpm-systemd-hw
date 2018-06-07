package com.gvisoc.hello;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 * HWaaS - Hello world as a Service
 */
public class Service extends TimerTask {
    static Logger LOGGER = LoggerFactory.getLogger(Service.class);
    private static final long DEFAULT_DELAY = 5000;
    private static final String DEFAULT_NAME = "World";
    private Configuration config;
    private long delay = -1;
    private String name = null;

    public Service() {
        Configurations configs = new Configurations();
        try {
            config = configs.properties(
                    getClass()
                            .getClassLoader()
                            .getResource("application.properties")
                            .getFile());
            delay = config.getLong("delay");
            name = config.getString("name");
        } catch (ConfigurationException e) {
            LOGGER.warn("Warning: no file \"application.properties\" in the classpath, using defaults");
        }
        finally {
            if (delay == -1)
                delay = DEFAULT_DELAY;
            if (name == null  || "".equals(name)) {
                name = DEFAULT_NAME;
            }
        }
    }

    public long getDelay() {
        return delay;
    }

    /**
     * Logs to /tmp/hello.log
     */
    public void run() {
        LOGGER.info("Hello, " + name + "!");
    }

    /**
     * Main method for the service
     * @param args -- the optional delay to run the service. Defaults to 5 sec.
     */
    public static void main(String[] args) {
        Service instance = new Service();
        final String semaphore = "";
        final Timer timer = new Timer();
        long delay = instance.getDelay();
        LOGGER.debug("Scheduling greeting service with delay (ms): " + delay);
        timer.schedule(instance, delay, delay);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Executed when kill -10 (SIGUSR1) in Linux, -30 in macOS.
                LOGGER.info("Exiting");
                LOGGER.debug("Signal to shutdown -- Gracefully shutting down");
                timer.cancel();
                synchronized (semaphore) {
                    semaphore.notify();
                }
            }
        });
        try {
            synchronized (semaphore) {
                semaphore.wait();
            }
        } catch (InterruptedException e) {

        }
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // BUG (own): This is currently ignored and the process exits with code 143 (killed)
        // To properly integrate with systemd we should be able to exit with 0.
        // To be done with sockets and TCP messages, or frameworks (not plain Java -- Spring something?).
        // At least the code ends gracefully.
        System.exit(0);
    }
}
