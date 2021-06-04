package com.example

import com.example.dto.UserDetailsModel
import com.example.dto.UserModel
import com.example.entity.User
import com.example.entity.UserToken
import com.example.repository.UserRepository
import com.example.repository.UserTokenRepository
import com.example.service.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Specification

class UserDetailsServiceSpec extends Specification {

    UserRepository userRepository = Mock();
    UserTokenRepository userTokenRepository = Mock();
    BCryptPasswordEncoder encoder = Mock();

    UserDetailsService service = new UserDetailsService(userRepository, userTokenRepository, encoder);

    def "test login after logout"() {
        given:
        def userId = 1L
        def token = "aa-bb-tt-ff"
        def returnedUser = User.newInstance(['id': userId, 'username': 'username', 'password': '$2a$10$noC1lzK.9BjhI.zO0ZDxxucRq3Zlh6oEkwnUUCPa1aiPeZD5QBS4a', 'description': 'desc'])
        def userModel = UserModel.newInstance(['username': 'username', 'password': 'passwd'])
        def userToken = UserToken.newInstance("user": returnedUser, "id": 1, "uuid": token, "active": false)

        when:
        def tokenReturned = service.login(userModel)

        then:
        1 * userRepository.findByUsername(userModel.getUsername()) >> Optional.of(returnedUser)
        1 * encoder.matches(userModel.getPassword(), returnedUser.getPassword()) >> true
        1 * userTokenRepository.findTopByUserIdOrderByIdDesc(returnedUser.getId()) >> Optional.of(userToken)
        1 * userTokenRepository.save(_ as UserToken) >> UserToken.newInstance("user": returnedUser, "id": 1, "uuid": "uu-ii-dd", "active": true)
        0 * _

        and:
        tokenReturned != null
        tokenReturned != token

        and:
        noExceptionThrown()
    }

    def "test login new"() {
        given:
        def userId = 1L
        def token = "aa-bb-tt-ff"
        def returnedUser = User.newInstance(['id': userId, 'username': 'username', 'password': '$2a$10$noC1lzK.9BjhI.zO0ZDxxucRq3Zlh6oEkwnUUCPa1aiPeZD5QBS4a', 'description': 'desc'])
        def userModel = UserModel.newInstance(['username': 'username', 'password': 'passwd'])
        def userToken = UserToken.newInstance("user": returnedUser, "id": 1, "uuid": token, "active": true)

        when:
        def tokenReturned = service.login(userModel)

        then:
        1 * userRepository.findByUsername(userModel.getUsername()) >> Optional.of(returnedUser)
        1 * encoder.matches(userModel.getPassword(), returnedUser.getPassword()) >> true
        1 * userTokenRepository.findTopByUserIdOrderByIdDesc(returnedUser.getId()) >> Optional.empty()
        1 * userTokenRepository.save(_ as UserToken) >> userToken
        0 * _

        and:
        tokenReturned == token

        and:
        noExceptionThrown()
    }

    def "test login error"() {
        given:
        def userId = 1L
        def token = "aa-bb-tt-ff"
        def returnedUser = User.newInstance(['id': userId, 'username': 'username', 'password': '$2a$10$noC1lzK.9BjhI.zO0ZDxxucRq3Zlh6oEkwnUUCPa1aiPeZD5QBS4a', 'description': 'desc'])
        def userModel = UserModel.newInstance(['username': 'username', 'password': 'passwd'])
        def userToken = UserToken.newInstance("user": returnedUser, "id": 1, "uuid": token, "active": isActive)

        when:
        service.login(userModel)

        then:
        1 * userRepository.findByUsername(userModel.getUsername()) >> Optional.of(returnedUser)
        1 * encoder.matches(userModel.getPassword(), returnedUser.getPassword()) >> isTheSamePassword
        n1 * userTokenRepository.findTopByUserIdOrderByIdDesc(returnedUser.getId()) >> Optional.of(userToken)
        0 * _

        and:
        thrown(BusinessException)

        where:
        n1  | isTheSamePassword       | isActive
        0   | false                   | _
        1   | true                    | true
    }

    def "get user success"() {
        given:
        def userId = 1L
        def returnedUser = User.newInstance(['id': userId, 'username': 'username', 'password': 'aa-bb-cc-66', 'description': 'desc'])
        def userDetails = UserDetailsModel.newInstance(['id': userId, 'username': 'username', 'description': 'desc'])

        when:
        def user = service.getUser(userId)

        then:
        1 * userRepository.findById(userId) >> Optional.of(returnedUser)
        0 * _

        and:
        user == userDetails
    }

    def "get user error"() {
        given:
        def userId = 1L

        when:
        service.getUser(userId)

        then:
        1 * userRepository.findById(userId) >> Optional.empty()
        0 * _

        and:
        thrown(BusinessException)
    }

    def "register new"() {
        given:
        def userId = 1L
        def user = User.newInstance(['id': userId, 'username': 'username', 'password': 'aa-bb-cc-66', 'description': 'desc'])
        def userModel = UserModel.newInstance(['username': 'username', 'password': 'passwd'])

        when:
        def returnedId = service.register(userModel)

        then:
        1 * userRepository.findByUsername(userModel.getUsername()) >> Optional.empty()
        1 * encoder.encode(userModel.getPassword()) >> "aa-bb-cc-66"
        1 * userRepository.save(_ as User) >> user
        0 * _

        and:
        returnedId == user.getId()
    }

    def "register existent"() {
        given:
        def userId = 1L
        def user = User.newInstance(['id': userId, 'username': 'username', 'password': 'aa-bb-cc-66', 'description': 'desc'])
        def userModel = UserModel.newInstance(['username': 'username', 'password': 'passwd'])

        when:
        service.register(userModel)

        then:
        1 * userRepository.findByUsername(userModel.getUsername()) >> Optional.of(user)
        0 * _

        and:
        thrown(BusinessException)
    }

    def "logout success"() {
        given:
        def userId = 1L
        def token = "aa-vv-hh-66"
        def user = User.newInstance(['id': userId, 'username': 'username', 'password': 'aa-bb-cc-66', 'description': 'desc'])
        def userToken = UserToken.newInstance("user": user, "id": 1, "uuid": token, "active": true)

        when:
        service.logout(token)

        then:
        1 * userTokenRepository.findByUuid(token) >> Optional.of(userToken)
        1 * userTokenRepository.save({it.active == false})
        0 * _

        and:
        noExceptionThrown()
    }

    def "logout error"() {
        given:
        def token = "aa-vv-hh-66"
        def user = User.newInstance(['id': 1, 'username': 'username', 'password': 'aa-bb-cc-66', 'description': 'desc'])
        def userToken = UserToken.newInstance("user": user, "id": 1, "uuid": token, "active": false)

        when:
        service.logout(token)

        then:
        n1 * userTokenRepository.findByUuid(token) >> Optional.of(userToken)
        n2 * userTokenRepository.findByUuid(token) >> Optional.empty()
        0 * _

        and:
        thrown(BusinessException)

        where:
        n1  | n2
        1   | 0
        0   | 1
    }

    def "get user by token"() {
        given:
        def token = "aa-vv-hh-66"
        def user = User.newInstance(['id': 1, 'username': 'username', 'password': 'aa-bb-cc-66', 'description': 'desc'])
        def userToken = UserToken.newInstance("user": user, "id": 1, "uuid": token, "active": true)

        when:
        def returnedUserDetails = service.getUserByToken(token)

        then:
        1 * userTokenRepository.findByUuid(token) >> Optional.of(userToken)
        0 * _

        and:
        returnedUserDetails.username == user.username
        returnedUserDetails.password == user.password
    }

    def "get user by token error"() {
        given:
        def token = "aa-vv-hh-66"

        when:
        service.getUserByToken(token)

        then:
        1 * userTokenRepository.findByUuid(token) >> Optional.empty()
        0 * _

        and:
        thrown(BusinessException)
    }
}