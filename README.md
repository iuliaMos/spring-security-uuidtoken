## Spring Security Example with UUID token

This example is a Spring Boot REST application with spring security.
It's functions are: 
 - register with username and password
 - login with credentials and receive a token
 - retrieve user details using token returned after login
 - logout using token

It has an in memory database H2 with some data initialized.
A complete Unit tests and Integration tests with Spock framework and Groovy.
It uses uuid token for logged session.
Password is saved encrypted.
For now only a single business exception is used and handled.
Request body are validated.
Each request that requires authentication should have 'Authorization' header.
No role is used to restrict data or REST endpoints.
It has a Docker file to run application in a docker container.
Tests are run on startup.

## Usage example

curl -d '{"username":"ana", "password":"ana"}'  -X POST http://localhost:8080/register => retrieve userId
curl -d '{"username":"ana", "password":"ana"}'  -X POST http://localhost:8080/login    => retrieve token (example: 85a5f04d-85a4-4e7d-8451-e5093ba1f948)
curl -H "Authorization: Bearer 85a5f04d-85a4-4e7d-8451-e5093ba1f948" -X GET http://localhost:8080/api/user/1  (the last component in path is userId)
curl -H "Authorization: Bearer 85a5f04d-85a4-4e7d-8451-e5093ba1f948" -X POST http://localhost:8080/logout

## Docker
docker build -t spring-security-uuidtoken . </br>
docker run -p 8080:8080 spring-security-uuidtoken
 


