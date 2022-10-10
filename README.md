# Splunk HEC API stub

Use to capture calls from https://github.com/splunk/splunk-library-javalogging

## Build
```
./gradlew build
```

## Run
```
java -jar build/libs/splunkstub-0.0.1-SNAPSHOT.jar
```

The service is running in port 8088.

## enable TLS

Generate keys:
```shell
keytool -genkeypair -alias stokpop -keyalg RSA -keysize 4096 \
    -validity 3650 -dname "CN=localhost" -keypass changeit -keystore keystore.p12 \
    -storeType PKCS12 -storepass changeit
```

Add to `application.properties`:

```properties
# enable/disable https
server.ssl.enabled=true
# keystore format
server.ssl.key-store-type=PKCS12
# keystore location
server.ssl.key-store=classpath:keystore/keystore.p12
# keystore password
server.ssl.key-store-password=changeit

# SSL protocol to use
server.ssl.protocol=TLS
# Enabled SSL protocols
server.ssl.enabled-protocols=TLSv1.2
```

## tcpdump

```shell
sudo tcpdump -B 16384 -Z $(whoami) -s0 -w /tmp/tcpdump.$(hostname -s).$(date +%Y%m%d.%H%M%S).pcap -i lo0 port 8088
```

## example config

In `pom.xml`:

```xml
<repositories>
    <repository>
        <id>splunk-artifactory</id>
        <name>Splunk Releases</name>
        <url>https://splunk.jfrog.io/splunk/ext-releases-local</url>
    </repository>
</repositories>
```
and

```xml
<dependency>
    <groupId>com.splunk.logging</groupId>
    <artifactId>splunk-library-javalogging</artifactId>
    <version>1.9.0</version>
</dependency>
````

In `logback-spring.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{dd-MM-yyyy'T'HH:mm:ss.SSS} %highlight(%-5level) %green([%20.20thread]) %yellow([%40.40logger{40}]) - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="splunk-hec-appender" class="com.splunk.logging.HttpEventCollectorLogbackAppender">
        <url>http://localhost:8088</url>
        <token>abc123</token>
        <index>afterburner</index>
        <disableCertificateValidation>true</disableCertificateValidation>
        <send_mode>parallel</send_mode>
        <batch_size_bytes>102400</batch_size_bytes>
        <batch_size_count>10</batch_size_count>
        <source>afterburner:log</source>
        <sourcetype>eafterburner:test:env</sourcetype>
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>[__time=%d][%d{"yyyy-MM-dd'T'HH:mm:ss,SSSXXX", UTC}] [%thread] [%-5level] [%logger{36}] %msg%n</Pattern>
        </layout>
    </appender>

    <root level="info">
        <appender-ref ref="Console" />
        <appender-ref ref="splunk-hec-appender" />
    </root>
    
</configuration>    
```