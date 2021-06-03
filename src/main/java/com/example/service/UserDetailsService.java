package com.example.service;

import com.example.BusinessException;
import com.example.dto.UserModel;
import com.example.entity.User;
import com.example.entity.UserToken;
import com.example.repository.UserRepository;
import com.example.repository.UserTokenRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    @NonNull
    private UserRepository userRepository;
    @NonNull
    private UserTokenRepository userTokenRepository;
    @NonNull
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public String login(final UserModel user) {
        //TODO find by password also
        final Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isEmpty()) {
            throw new BusinessException("This user is not registered");
        }

        final User userDb = userOptional.get();
        final Optional<UserToken> userToken = userTokenRepository.findTopByUserIdOrderByIdDesc(userDb.getId());

        if (userToken.isEmpty()) {
            return newToken(userDb).getUuid();
        }

        final UserToken topToken = userToken.get();

        if (topToken.getActive()) {
            throw new BusinessException("User aleardy logged in");
        }

        return getUserToken(userDb);
    }

    private String getUserToken(final User user) {
        final Optional<UserToken> userToken = userTokenRepository.findTopByUserIdOrderByIdDesc(user.getId());

        if (userToken.isPresent() && userToken.get().getActive()) {
            throw new BusinessException("User aleardy logged in");
        }

        return newToken(user).getUuid();
    }

    private UserToken newToken(final User user) {
        UserToken newToken = new UserToken();
        newToken.setUser(user);
        newToken.setActive(Boolean.TRUE);
        newToken.setUuid(UUID.randomUUID().toString());
        return userTokenRepository.save(newToken);
    }

    public Long register(final UserModel user) {
        final Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isPresent()) {
            throw new BusinessException("User already exists");
        }

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        newUser.setDescription(user.getDescription());

        return userRepository.save(newUser).getId();
    }

    public void logout(final String token) {
        Optional<UserToken> userToken = userTokenRepository.findByUuid(token);
        if (userToken.isEmpty() || !userToken.get().getActive()) {
            throw new BusinessException("Invalid token to logout");
        }
        UserToken userTokenToInvalidate = userToken.get();

        log.info("logout user {}", userTokenToInvalidate.getUser().getId());

        userTokenToInvalidate.setActive(false);
        userTokenRepository.save(userTokenToInvalidate);
    }

    public User getUser(final Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            throw new BusinessException("No such user");
        }
        return user.get();
    }

    public UserDetails getUserByToken(final String token) {
        Optional<UserToken> userToken = userTokenRepository.findByUuid(token);
        if (userToken.isEmpty()) {
            throw new BusinessException("Invalid token");
        }

        User userDb = userToken.get().getUser();
        return new org.springframework.security.core.userdetails.User(userDb.getUsername(), userDb.getPassword(),
                true, true, true, true, AuthorityUtils.createAuthorityList("USER"));
    }
}
