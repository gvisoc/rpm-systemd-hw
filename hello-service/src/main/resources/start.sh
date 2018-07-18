#!/bin/bash
nohup java -cp /opt/hello:/opt/hello/* -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false com.gvisoc.hello.Start > /tmp/hello.out 2>&1 &

echo $! > /tmp/hello.pid