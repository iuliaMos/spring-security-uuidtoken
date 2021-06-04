## Spring Security Example with UUID token

This example is a Spring Boot REST application with spring security.
It's functions are: 
 - register with username and password
 - login with credentials and receive a token
 - retrieve user details using token returned after login
 - logout using token

It has an in memory database H2 with some data initialized. </br>
A complete Unit tests and Integration tests with Spock framework and Groovy. </br>
It uses uuid token for logged session. </br>
Password is saved encrypted. </br>
For now only a single business exception is used and handled. </br>
Request body are validated. </br>
Each request that requires authentication should have 'Authorization' header. </br>
No role is used to restrict data or REST endpoints. </br>
It has a Docker file to run application in a docker container. </br>
Tests are run on startup. </br>

## Usage example

curl -H "Content-Type: application/json" -d '{"username":"ana", "password":"ana"}'  -X POST http://localhost:8080/register => retrieve userId </br>
curl -H "Content-Type: application/json" -d '{"username":"ana", "password":"ana"}'  -X POST http://localhost:8080/login    => retrieve token (example: 85a5f04d-85a4-4e7d-8451-e5093ba1f948) </br>
curl -H "Content-Type: application/json" -H "Authorization: Bearer 85a5f04d-85a4-4e7d-8451-e5093ba1f948" -X GET http://localhost:8080/api/user/1  (the last component in path is userId) </br>
curl -H "Content-Type: application/json" -H "Authorization: Bearer 85a5f04d-85a4-4e7d-8451-e5093ba1f948" -X POST http://localhost:8080/api/logout </br>

## Docker
docker build -t spring-security-uuidtoken . </br>
docker run -p 8080:8080 spring-security-uuidtoken
 


