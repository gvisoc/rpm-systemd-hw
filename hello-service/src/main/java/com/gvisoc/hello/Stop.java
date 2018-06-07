package com.gvisoc.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class Stop  {
    static Logger LOGGER = LoggerFactory.getLogger(Stop.class);
    //ToDo: an enum for these, common for Start and Stop.
    private static final int SYSTEM_OK = 0;
    private static final int SYSTEM_ERROR_JMX = 6;

    public static void main(String[] args)
    {
        int status;
        try
        {
            LOGGER.info("Connect to JMX service.");
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:9999/jmxrmi");
            JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

            ObjectName mbeanName = new ObjectName("com.gvisoc:type=StopMonitor");
            StopMonitorMBean mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanName, StopMonitorMBean.class, true);

            LOGGER.info("Connected.");
            mbeanProxy.stop();
            LOGGER.info("Done.");
            status = SYSTEM_OK;
        }
        catch(Exception e)
        {
            LOGGER.error("Caught JMX Error: ", e);
            status = SYSTEM_ERROR_JMX;
        }
        System.exit(status);
    }
}
