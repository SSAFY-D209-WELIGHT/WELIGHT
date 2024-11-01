package com.d209.welight.domain.user.repository;

import com.d209.welight.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // find
    Optional<User> findByUserUid(Long userUid);
    Optional<User> findByUserId(String userId);
//    Optional<User> findByUserRefreshToken(String userRefreshToken);
//    Optional<User> findByUserUid(Long userUid);

    // exists
    boolean existsByUserId(String userId);
    boolean existsByUserNickname(String userNickname);

    // delete
    void deleteByUserId(String userId);

}
