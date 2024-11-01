package com.d209.welight.domain.display.repository;

import com.d209.welight.domain.display.entity.Display;
import com.d209.welight.domain.display.entity.displaylike.DisplayLike;
import com.d209.welight.domain.display.entity.displaylike.DisplayLikeId;
import com.d209.welight.domain.display.entity.displaylike.DisplayLikeId;
import com.d209.welight.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DisplayLikeRepository extends JpaRepository<DisplayLike, DisplayLikeId> {
    // userUid와 displayUid로 DisplayStorage가 이미 존재하는지 확인
    boolean existsByUserAndDisplay(User user, Display display);
    void deleteByUserAndDisplay(User user, Display display);
}