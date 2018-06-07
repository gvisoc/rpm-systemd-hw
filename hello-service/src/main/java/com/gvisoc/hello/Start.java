package com.gvisoc.hello;

import com.gvisoc.hello.task.HelloTask;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.Timer;



public class Start {
    static Logger LOGGER = LoggerFactory.getLogger(Start.class);
    //ToDo: an enum for these, common for Start and Stop.
    private static final int SYSTEM_ERROR_JMX_DUPLICATED_INSTANCE = 3;
    private static final int SYSTEM_ERROR_JMX_COMPLIANCE = 5;
    private static final int SYSTEM_ERROR_JMX_REGISTER = 4;
    private static final int SYSTEM_OK = 0;
    private static final int SYSTEM_ERROR_SIGINT = 1;
    private static final int SYSTEM_ERROR_JMX_NAME = 2;
    /**
     * Main method for the service
     */
    public static void main(String[] args) {
        String name = null;
        long delay = -1;


        Configuration config = getConfig();
        if (config != null) {
            delay = config.getLong("delay", 5000);
            name = config.getString("name", "World");
        }

        HelloTask instance = new HelloTask(name);

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
        ObjectName name;
        try {
            name = new ObjectName("com.gvisoc:type=StopMonitor");
            server.registerMBean(monitor, name);
            synchronized (semaphore) {
                try {
                    LOGGER.debug("Wait for exit command");
                    semaphore.wait();
                    status = SYSTEM_OK;
                } catch (InterruptedException e) {
                    LOGGER.debug("Wait cancelled by system signal -- Abnormal exit");
                    status = SYSTEM_ERROR_SIGINT;
                }
            }
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
            LOGGER.error("Malformed object name. Exiting...");
            e.printStackTrace();
            status = SYSTEM_ERROR_JMX_NAME;
        } catch (InstanceAlreadyExistsException e) {
            LOGGER.error("Instance already exists. Exiting...");
            e.printStackTrace();
            status = SYSTEM_ERROR_JMX_DUPLICATED_INSTANCE;
        } catch (MBeanRegistrationException e) {
            LOGGER.error("Error registering stop listener. Exiting...");
            e.printStackTrace();
            status = SYSTEM_ERROR_JMX_REGISTER;
        } catch (NotCompliantMBeanException e) {
            LOGGER.error("Non-Compliant MBean. Exiting...");
            e.printStackTrace();
            status = SYSTEM_ERROR_JMX_COMPLIANCE;
        }
        return status;
    }

    private static Configuration getConfig() {
        Configurations configs = new Configurations();
        Configuration config = null;
        try {
            config = configs.properties(
                    HelloTask.class
                            .getClassLoader()
                            .getResource("application.properties")
                            .getFile());
        } catch (ConfigurationException e) {
            LOGGER.warn("Warning: no file \"application.properties\" in the classpath, using defaults");
        }
        return config;
    }
}
