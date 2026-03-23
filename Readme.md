# To run this application w/o updating the JAVA_HOME if you # already #have java_home setup for some other service. Otherwise you just have to run it the usual way

$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.2"

.\mvnw.cmd spring-boot:run

# running via a docker command line

# Correct — maps host 6060 to container 8082 (where the app actually is)
port 8082 is exposed here: 
    1. src\main\resources\application.properties
    2. Dockerfile

docker run -d -p 6060:8082 --name lewis-usda-app haile819/usdahubproxy:latest

The format is always -p HOST_PORT:CONTAINER_PORT — the right side must match EXPOSE in the Dockerfile and server.port in application.properties. 
All three need to agree Locations
    Value application.properties → server.port8082 Dockerfile → EXPOSE 8082docker run → -p 6060:8082 

Run command:
docker run -d -p 6060:8082 --name lewis-usda-app haile819/usdahubproxy:latest

# to stop and remove container
docker stop lewis-usda-app
docker rm lewis-usda-app

powershell command to run *.java files.
    # Compile only
    .\mvnw clean compile

    # Compile + run tests
    .\mvnw clean test

    # Compile + package into a JAR (skipping tests)
    .\mvnw clean package -DskipTests

    # Compile + run the app
    .\mvnw clean spring-boot:run