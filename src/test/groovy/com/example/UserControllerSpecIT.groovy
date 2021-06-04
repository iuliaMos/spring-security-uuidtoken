package com.example

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.*
import spock.lang.Specification

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerSpecIT extends Specification {

    @LocalServerPort
    private int port;

    def "test logout"() {
        given:
        TestRestTemplate restTemplate = new TestRestTemplate()
        def headers = new HttpHeaders()
        headers.set("Authorization", tokenAuth)


        when:
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/api/logout", HttpMethod.POST, new HttpEntity<>(null, headers), String.class);

        then:
        assertThat(response.getStatusCode(), equalTo(statusCode))

        where:
        tokenAuth               | statusCode
        "Bearer aa-vv-ff-ee-77" | HttpStatus.OK
        "Bearer invalid"        | HttpStatus.INTERNAL_SERVER_ERROR
        "aa-vv-ff-ee-77"        | HttpStatus.INTERNAL_SERVER_ERROR
    }

    def "get User Details"() {
        given:
        TestRestTemplate restTemplate = new TestRestTemplate()
        def headers = new HttpHeaders()
        headers.set("Authorization", "Bearer bb-nn-cc-55")


        when:
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:" + port + "/api/user/" + userId, HttpMethod.GET, new HttpEntity<>(null, headers), String.class);

        then:
        assertThat(response.getStatusCode(), equalTo(statusCode))

        where:
        userId | statusCode
        1      | HttpStatus.OK
        2      | HttpStatus.OK
        100    | HttpStatus.INTERNAL_SERVER_ERROR
    }

}