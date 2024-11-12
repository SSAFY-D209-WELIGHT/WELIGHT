package com.d209.welight.domain.display.repository;

import com.d209.welight.domain.display.entity.Display;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface DisplayRepository extends JpaRepository<Display, Long> {

    List<Display> findAllByOrderByCreatedAtDesc();
    List<Display> findAllByOrderByLikeCountDesc();
    List<Display> findAllByOrderByDownloadCountDesc();

    List<Display> findByCreatorUid(Long creatorUid);

    @Query("SELECT d FROM Display d WHERE d.displayName LIKE %:keyword%")
    List<Display> searchByName(String keyword);

    @Modifying
    @Transactional
    @Query("UPDATE Display d SET d.displayLikeCount = d.displayLikeCount + 1 WHERE d.displayUid = :displayId")
    void incrementLikeCount(Long displayId);

    @Modifying
    @Transactional
    @Query("UPDATE Display d SET d.displayLikeCount = d.displayLikeCount - 1 WHERE d.displayUid = :displayId")
    void decrementLikeCount(Long displayId);

    @Modifying
    @Transactional
    @Query("UPDATE Display d SET d.displayDownloadCount = d.displayDownloadCount + 1 WHERE d.displayUid = :displayId")
    void incrementDownloadCount(Long displayId);
}
