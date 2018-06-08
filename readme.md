# RPM installing a Java "Hello Service" in systemd
This repo creates a RPM package explicitly depending on Java (pulling the installation of Java >= 1.8 as a dependency), and setting up a systemd service for Red Hat Linux systems and similar (Fedora, CentOS,...). 

It shows how to use environment variables for inject in the service configuration, and how to comply with Java exit values with systemd's expectations. This is done by coding Start and Stop commands that finish doing `System.exit(status)` calls with proper compliant statuses.

## Contents
### Java code
The module `hello-service` contains a Maven project of a plain Java application that executes a "Hello Task" every three seconds. This task (`com.gvisoc.task.Hello`) is a `TimerTask` and is wrapped in two operation commands that comply with the Exit codes expected by systemd (0 for OK, anything greater for other conditions).

The process wrappers are two classes, `com.gvisoc.hello.Start` and `com.gvisoc.hello.Stop`. The first one creates the task and controls the timer, exposing a Management Bean (see `com.gviosc.hello.StopMonitor`) with a method `stop`. This method stop is called via JMX from `com.gvisoc.hello.Stop`. 

The configuration of the process is dine via the Apache Commons library for configuration, in order to be able to refer environment variables written by an external file, set up in the Unit File (see [sytemd unit file](#systemd-unit-file) and [Use](#use)). This file is touched by the RPM installation process (defined in the Maven POM file, see [RPM configuration](#rpm-configuration)), o that is created if didn't exist. No environment variables are provided by default, that is something to be done with configuration managers like Ansible (to be explored).

### Start and stop scripts
Two scripts are located under `hello-service/src/main/resources`: `start.sh` and `stop.sh`.

### systemd unit file
The unit file is located under `hello-service/src/systemd`: `hello.service`.

### RPM configuration
The file reponsible for the configuration is `hello-service/pom.xml` and uses MojoHaus' `rpm-maven-plugin`.

## Use
1. Build the project and create the RPM package by executing the usual Maven tasks, `mvn clen compile package`
2. Install the package in your system (load it and run `sudo yum install <rpm>`). If there is no Java runtime installed, it should install one of version 1.8 or greater.
3. Optionally, write the property `USER=gvisoc`, or with any other String value, to the environment file `/var/hello-environment.properties`
4. Execute the following command to refresh the systemd units without rebooting: `sudo systemctl daemon-reload`.    
5. Start the service with `sudo systemctl start hello`
6. Check the output of the service with `tail -f /tmp/hello.log`:

```
10:26:07.603 [com.gvisoc.hello.Start.main()] DEBUG com.gvisoc.hello.Start - Scheduling greeting service with delay (ms): 3000
10:26:07.667 [com.gvisoc.hello.Start.main()] DEBUG com.gvisoc.hello.Start - Wait for exit command
10:26:10.612 [Timer-0] INFO  com.gvisoc.hello.task.HelloTask - Hello, gvisoc!
10:26:13.612 [Timer-0] INFO  com.gvisoc.hello.task.HelloTask - Hello, gvisoc!
10:26:16.611 [Timer-0] INFO  com.gvisoc.hello.task.HelloTask - Hello, gvisoc!
10:26:19.613 [Timer-0] INFO  com.gvisoc.hello.task.HelloTask - Hello, gvisoc!
```
7. Restart the service with `sudo sytemctl restart hello`. Stop the service with `sudo systemctl stop hello`. Check the result in the log (more logs from the `Stop` command may appear):
```
10:26:43.628 [Timer-0] INFO  com.gvisoc.hello.task.HelloTask - Hello, gvisoc!
10:26:44.855 [com.gvisoc.hello.Start.main()] DEBUG com.gvisoc.hello.Start - Exiting with status: 0
```
8. Check the service status with `sudo systemctl status hello.service -l`.

## References
Check this material:

1. [MojoHaus' RPM Plugin](https://www.mojohaus.org/rpm-maven-plugin/)
2. [systemd unit file reference](https://access.redhat.com/documentation/en-us/red_hat_enterprise_linux/7/html/system_administrators_guide/sect-managing_services_with_systemd-unit_files)
3. For further steps and check / improve portability, check [systemd unit packaging](https://access.redhat.com/documentation/en-us/red_hat_enterprise_linux/7/html/system_administrators_guide/sect-managing_services_with_systemd-unit_files)

## Limitations
* No Java Tests (sorry). The focus of this repo is the RPM and systemd part.
* No SSL JMX communications. This is not a *real* problem for this code because the `stop()` exposed operation has no parameters (hence no information had to be encrypted) and, regarding the authenticated execution, no one else than a system administrator should have access to the stop command or the machine to run `jconsole`. In other words, that problem has been left to the system administrators regarding users and permissions. No drama as any of them can always run kill.
* There is (a little) room for unexpected results when running similar code in a CI/CD pipeline or in a proper Red Hat Linux system, as the RPM was created in a Mac by installing `rpm` with Homebrew with `brew install rpm`. It should run OK, but please fill a bug if it doesn't.
* The result was tested only in CentOS 7.
* No further limitations other than those derived from the design itself, and Windows rpm building. Although not tested / explored, this last can be done with a Cygwin environment or similar, or even with Ubuntu for Windows if you have Windows 10. 

Feedback and pull requests are welcome.

