spring-boot-docker-camel
=====================

Demonstrates the deployment of Camel within Spring Boot and integration with Docker using various integration technologies

# Project Overview

This project demonstrates the integration capabilities of Apache Camel within Spring Boot and Docker. The Camel context is initialized within the Spring Boot framework via the Camel Spring Boot Component with two primary functions: proxy inquiries via REST to Docker and to broadcast events produced by Docker to a websockets endpoint with the Async HTTP Client for websockets. Each integration with Docker leverages the Camel Docker component. Spring Boot also exposes a servlet to handle inbound REST requests to Camel along with managing the websockets endpoint.

A webpage has been provided to expose the operations which can be made to Docker and to consume messages published to the websockets endpoint 

## Docker Proxy

A REST endpoint is exposed using the Camel Rest DSL to consume requests destined for Docker. A combination of configuration within the Rest DSL and content based routing determines valid and invalid requests. Invalid requests return an appropriate message to the user. Valid request undergo message enrichment to allow for the appropriate invocation of Docker. Results are returned to the user in JSON format.  

## Broadcast Docker Events

The Docker REST API exposes a service which allows for the monitoring of events within the Docker daemon. A camel route is configured to consume messages from this service which are then sent to a websockets endpoint. 

# Camel Components 

The following Camel components are leveraged:

* [Spring Boot](http://camel.apache.org/spring-boot.html)
* [Docker](http://camel.apache.org/docker.html)
* [Rest DSL](http://camel.apache.org/rest-dsl.html)
* [Async HTTP Client (Websockets)](http://camel.apache.org/ahc-ws.html)


# Prerequisites

* Java 6+
* Maven
* Docker
	* The REST API of a local or remote instance of Docker must be available

# Configuration

This section describes the components which can be configured within the application
	
## Spring Boot

External configurations can be provided to Spring Boot in the `application.properties` file located in `src/main/resources`. These can be referenced by Spring Boot itself or as an external properties within Came;

## DockerThe location of Docker must be configured using the `docker.server` and `docker.port` properties within the `application.properties` 

# Build and Deployment

Since the project is leveraging Spring Boot, it can be deployed in a number of different ways. The following sections describe these options

## Command Line

Use the Spring Boot Maven Plugin to launch the application. Run the following command in the project directory

    mvn clean spring-boot:run 

## Tomcat

The project can be built as a Web Archive (.war) and deployed to Tomcat.

### Build and deploy

Build and package the project using the following command

    mvn clean install 

Copy the .war file from the project target directory to the Tomcat deploy folder

# Running the Application

Once successfully deployed, the application will be available at:

[http://localhost:8080/spring-boot-docker-camel](http://localhost:8080/spring-boot-docker-camel)

## Displaying Events

Events from Docker are displayed in a text box within the sample web page. The following command can be used to create a minimalistic container producing events to the websockets endpoint:

`docker run -it --rm busybox /bin/sh`

A new container should be created and events should be visible on the webpage. From the interactive shell inside the container created above, enter `exit` to destroy the container, thus producing additional events

## Viewing Docker Statistics

Certain statistics of Docker have been exposed through Camel route via REST services. Links are available on the webpage to invoke these services. Responses are returned in raw ison format