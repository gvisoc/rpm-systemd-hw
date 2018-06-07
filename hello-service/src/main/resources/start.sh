#!/bin/bash
nohup java -cp /opt/hello:/opt/hello/* com.gvisoc.hello.Service -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false > /tmp/hello.out 2>&1 &
echo $! > /var/run/hello.pid