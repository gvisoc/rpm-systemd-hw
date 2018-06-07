package com.gvisoc.hello;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class Stop  {
    //static Logger LOGGER = LoggerFactory.getLogger(Service.class);
    public static void main(String[] args)
    {
        try
        {
            // connecting to JMX
            //LOGGER.info("Connect to JMX service.");
            System.out.println("Connect to JMX service.");
            JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:9999/jmxrmi");
            JMXConnector jmxc = JMXConnectorFactory.connect(url, null);
            MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

            // Construct proxy for the the MBean object
            ObjectName mbeanName = new ObjectName("com.gvisoc:type=StopMonitor");
            StopMonitorMBean mbeanProxy = JMX.newMBeanProxy(mbsc, mbeanName, StopMonitorMBean.class, true);

            //LOGGER.info("Connected.");
            System.out.println("Connected.");
            mbeanProxy.stop();
            //jmxc.close();
            //LOGGER.info("Done.");
            System.out.println("Done");
        }
        catch(Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
