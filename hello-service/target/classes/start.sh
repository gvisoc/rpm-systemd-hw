#!/bin/bash
nohup java -cp /opt/hello:/opt/hello/* com.gvisoc.hello.Service > /tmp/hello.out 2>&1 &
echo $! > /var/run/hello.pid