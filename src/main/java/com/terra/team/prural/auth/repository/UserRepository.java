package com.terra.team.prural.auth.repository;

import com.terra.team.prural.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.verificationToken = :token AND u.tokenExpirationTime > :currentTime")
    Optional<User> findByVerificationTokenAndNotExpired(@Param("token") String token, @Param("currentTime") Date currentTime);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.enabled = true")
    Optional<User> findByEmailAndEnabled(@Param("email") String email);
    
    Optional<User> findByResetPasswordToken(String token);
}
