## POC of action monitor with ActiveMQ and Websockets

This is a POC for an action monitor. It enables two users to communicate between themselves.

This application consists of:

* Spring boot application
* ActiveMQ
* MySQL database 

Information about the application:

    /actuator/info

Application health:

    /actuator/health
    
Configuration:

    /src/main/resources/application.properties

For more information see ImplementationArchitecture.pdf

## Minimum commands to run the example

### Build docker image of the application

    ./mvnw verify dockerfile:build

### Run application with all of it's dependencies

    docker-compose -f src/main/docker/app.yml up --build -d
    
### Check logs

    docker-compose -f src/main/docker/app.yml logs -f
    