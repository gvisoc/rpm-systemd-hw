%define __jar_repack 0
Name: hello-service
Version: 0.0.1
Release: 1
Summary: my-app
License: (c) null
Group: Application
Requires: java >= 1.8.0
autoprov: yes
autoreq: yes
BuildArch: noarch
BuildRoot: /Users/gvisoc/workspaces/rpm-systemd-hw/hello-service/target/rpm/hello-service/buildroot

%description

%install

if [ -d $RPM_BUILD_ROOT ];
then
  mv /Users/gvisoc/workspaces/rpm-systemd-hw/hello-service/target/rpm/hello-service/tmp-buildroot/* $RPM_BUILD_ROOT
else
  mv /Users/gvisoc/workspaces/rpm-systemd-hw/hello-service/target/rpm/hello-service/tmp-buildroot $RPM_BUILD_ROOT
fi

%files

%attr(-,gvisoc,-) "/opt/hello/"
  "/etc/systemd/system//hello.service"

%post
chmod uo+x /opt/hello/*.sh;touch /var/hello-environment.properties
