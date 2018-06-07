# RPM HelloWorld
This is an exploratory repo for creating a RPM package depending on Java (pulling Java >= 1.8 as a dependency).

It was tested on a CentOS virtual machine. The RPM is created in a Mac by installing `rpm` with Homebrew:

```brew install rpm```

## Use
1. Create the RPM package by executing the maven task `mvn package`
2. Install the package in your system
3. Optionally, write the property `USER=gvisoc` with any other value, to the environment file `/var/hello-environment.properties`
4. Execute the following command to refresh the systemd units without rebooting: `sudo systemctl daemon-reload`.
5. Start the service with `sudo systemctl start hello`
6. Check the output of the service with `tail -f /tmp/hello.log`:

```
19:06:18.054 [com.gvisoc.hello.Service.main()] DEBUG com.gvisoc.hello.Service - Scheduling greeting service with delay (ms): 3000
19:06:21.060 [Timer-0] INFO  com.gvisoc.hello.Service - Hello, gvisoc!
19:06:24.063 [Timer-0] INFO  com.gvisoc.hello.Service - Hello, gvisoc!
19:06:27.066 [Timer-0] INFO  com.gvisoc.hello.Service - Hello, gvisoc!
19:06:30.070 [Timer-0] INFO  com.gvisoc.hello.Service - Hello, gvisoc!
19:06:33.071 [Timer-0] INFO  com.gvisoc.hello.Service - Hello, gvisoc!
19:06:36.076 [Timer-0] INFO  com.gvisoc.hello.Service - Hello, gvisoc!
```
7. Stop the service with `sudo systemctl stop hello`. Check the result in the log:
```
19:07:18.116 [Timer-0] INFO  com.gvisoc.hello.Service - Hello, gvisoc!
19:07:19.132 [Thread-2] INFO  com.gvisoc.hello.Service - Exiting
19:07:19.132 [Thread-2] DEBUG com.gvisoc.hello.Service - Signal to shutdown -- Gracefully shutting down
```
8. Check the service status with `sudo systemctl status hello.service -l`.

## Limitations
A proper shutdown of the service has not been properly implemented at this moment. Although the code finishes gracefully in a plain raw Java way, the exit code is 143 instead of 0 (as it is killed with `kill -15`). A proper exit should be implemented either with socket servers (plain old Java) or with the help of some other frameworks or means.

