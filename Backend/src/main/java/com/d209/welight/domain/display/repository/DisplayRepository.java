package com.d209.welight.domain.display.repository;

import com.d209.welight.domain.display.entity.Display;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.d209.welight.domain.user.entity.User;

import java.util.Optional;


@Repository
public interface DisplayRepository extends JpaRepository<Display, Long> {

    Optional<Display> findByDisplayThumbnailUrl(String defaultThumbnailUrl);
    Optional<Display> findByDisplayUid(Long displayUid);
    
    // 게시 여부가 1인 디스플레이만 조회
    Page<Display> findAllByDisplayIsPostedTrue(Pageable pageable);


    @Query("SELECT d FROM Display d " +
            "JOIN DisplayStorage ds ON d = ds.display " +
            "WHERE ds.user = :user")
    Page<Display> findAllStoredByUser(@Param("user") User user, Pageable pageable);

}
