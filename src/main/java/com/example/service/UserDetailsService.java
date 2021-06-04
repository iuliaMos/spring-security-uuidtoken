package com.example.service;

import com.example.BusinessException;
import com.example.dto.UserDetailsModel;
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
        final User userDb = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new BusinessException("This user is not registered"));

        if (!bCryptPasswordEncoder.matches(user.getPassword(), userDb.getPassword())) {
            throw new BusinessException("This user is not registered");
        }

        final Optional<UserToken> userToken = userTokenRepository.findTopByUserIdOrderByIdDesc(userDb.getId());

        if (userToken.isEmpty()) {
            return newToken(userDb).getUuid();
        }

        final UserToken topToken = userToken.get();

        if (topToken.getActive()) {
            throw new BusinessException("User already logged in");
        }

        return newToken(userDb).getUuid();
    }

    private UserToken newToken(final User user) {
        UserToken newToken = new UserToken();
        newToken.setUser(user);
        newToken.setActive(Boolean.TRUE);
        newToken.setUuid(UUID.randomUUID().toString());
        return userTokenRepository.save(newToken);
    }

    private String encodePassword(final String password) {
        return bCryptPasswordEncoder.encode(password);
    }

    public Long register(final UserModel user) {
        final Optional<User> userOptional = userRepository.findByUsername(user.getUsername());
        if (userOptional.isPresent()) {
            throw new BusinessException("User already exists");
        }

        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(encodePassword(user.getPassword()));
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

    public UserDetailsModel getUser(final Long id) {
        final User user = userRepository.findById(id).orElseThrow(() -> new BusinessException("No such user"));
        return new UserDetailsModel(user.getId(), user.getUsername(), user.getDescription());
    }

    public UserDetails getUserByToken(final String token) {
        User userDb = userTokenRepository.findByUuid(token)
                .filter(userToken -> userToken.getActive())
                .map(UserToken::getUser).orElseThrow(() -> new BusinessException("Invalid token"));

        return new org.springframework.security.core.userdetails.User(userDb.getUsername(), userDb.getPassword(),
                true, true, true, true, AuthorityUtils.createAuthorityList("USER"));
    }
}
