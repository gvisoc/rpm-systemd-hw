package com.gvisoc.hello.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

/**
 * HWaaS - Hello world as a HelloTask (TimerTask)
 */
public class HelloTask extends TimerTask {
    static Logger LOGGER = LoggerFactory.getLogger(HelloTask.class);
    private String name = null;

    public HelloTask(String name) {
        this.name = name;
    }


    /**
     * Logs to /tmp/hello.log
     */
    public void run() {
        LOGGER.info("Hello, " + name + "!");
    }
}
