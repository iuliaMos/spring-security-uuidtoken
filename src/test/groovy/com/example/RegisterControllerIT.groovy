package com.example

import com.example.dto.UserModel
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegisterControllerIT extends Specification {

    @LocalServerPort
    private int port;

    def "test register" () {
        given:
        TestRestTemplate restTemplate = new TestRestTemplate()

        when:
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/register", HttpMethod.POST, new HttpEntity<>(userModel), String.class);

        then:
        assertThat(response.getStatusCode(), equalTo(statusCode))

        where:
        userModel                                                               | statusCode
        UserModel.newInstance(['username': 'username', 'password': 'password']) | HttpStatus.OK
        UserModel.newInstance(['username': 'aa', 'password': 'abc'])            | HttpStatus.INTERNAL_SERVER_ERROR
        UserModel.newInstance(['username': 'errorNoPassword'])                  | HttpStatus.INTERNAL_SERVER_ERROR
    }

    def "test login error" () {
        given:
        TestRestTemplate restTemplate = new TestRestTemplate()

        when:
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/login", HttpMethod.POST, new HttpEntity<>(userModel), String.class);

        then:
        assertThat(response.getStatusCode(), equalTo(statusCode))

        where:
        userModel                                                               | statusCode
        UserModel.newInstance(['username': 'usernameError' ])                   | HttpStatus.INTERNAL_SERVER_ERROR
        UserModel.newInstance(['username': 'aa', 'password': 'abc'])            | HttpStatus.INTERNAL_SERVER_ERROR
    }

    def "test login succes" () {
        given:
        TestRestTemplate restTemplate = new TestRestTemplate()
        def userModel = UserModel.newInstance(['username': 'userSuccess', 'password': 'password'])

        when:
        restTemplate.exchange("http://localhost:" + port + "/register", HttpMethod.POST, new HttpEntity<>(userModel), String.class);
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/login", HttpMethod.POST, new HttpEntity<>(userModel), String.class);

        then:
        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK))
    }

}
