package com.gvisoc.hello;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

/**
 * HWaaS - Hello world as a Service
 */
public class Service extends TimerTask {
    static Logger LOGGER = LoggerFactory.getLogger(Service.class);
    private String name = null;

    public Service(String name) {
        this.name = name;
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
        String name = null;
        long delay = -1;


        Configuration config = getConfig();
        if (config != null) {
            delay = config.getLong("delay", 5000);
            name = config.getString("name", "World");
        }

        Service instance = new Service(name);

        LOGGER.debug("Scheduling greeting service with delay (ms): " + delay);
        Timer timer = new Timer();
        timer.schedule(instance, delay, delay);

        int status = waitForStop();
        LOGGER.debug("Exiting with status: " + status);
        timer.cancel();
        System.exit(status);
    }

    private static int waitForStop() {
        int status;
        final String semaphore = "";
        StopMonitor monitor = new StopMonitor(semaphore);
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = null;
        try {
            name = new ObjectName("com.gvisoc:type=StopMonitor");
            server.registerMBean(monitor, name);
            synchronized (semaphore) {
                try {
                    LOGGER.debug("Wait for exit command");
                    semaphore.wait();
                    status = 0;
                } catch (InterruptedException e) {
                    LOGGER.debug("Wait cancelled by system signal -- Abnormal exit");
                    status = 1;
                }
            }
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
            LOGGER.error("Malformed object name. Exiting...");
            e.printStackTrace();
            status = 2;
        } catch (InstanceAlreadyExistsException e) {
            LOGGER.error("Instance already exists. Exiting...");
            e.printStackTrace();
            status = 3;
        } catch (MBeanRegistrationException e) {
            LOGGER.error("Error registrating stop listener. Exiting...");
            e.printStackTrace();
            status = 4;
        } catch (NotCompliantMBeanException e) {
            LOGGER.error("Non-Compliant MBean. Exiting...");
            e.printStackTrace();
            status = 5;
        }
        return status;
    }

    private static Configuration getConfig() {
        Configurations configs = new Configurations();
        Configuration config = null;
        try {
            config = configs.properties(
                    Service.class
                            .getClassLoader()
                            .getResource("application.properties")
                            .getFile());
        } catch (ConfigurationException e) {
            LOGGER.warn("Warning: no file \"application.properties\" in the classpath, using defaults");
        }
        return config;
    }
}
