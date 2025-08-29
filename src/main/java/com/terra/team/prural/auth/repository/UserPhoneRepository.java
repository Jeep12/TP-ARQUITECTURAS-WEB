package com.terra.team.prural.auth.repository;

import com.terra.team.prural.auth.entity.UserPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPhoneRepository extends JpaRepository<UserPhone, Long> {
    
    List<UserPhone> findByUserId(Long userId);
    
    @Query("SELECT COUNT(up) FROM UserPhone up WHERE up.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    Optional<UserPhone> findByIdAndUserId(Long id, Long userId);
    
    @Query("SELECT up FROM UserPhone up WHERE up.user.id = :userId AND up.isPrimary = true")
    Optional<UserPhone> findPrimaryPhoneByUserId(@Param("userId") Long userId);
}
