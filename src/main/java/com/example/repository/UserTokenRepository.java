package com.example.repository;

import com.example.entity.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    Optional<UserToken> findTopByUserIdOrderByIdDesc(Long userId);

    Optional<UserToken> findByUuid(String token);
}
