package com.d209.welight.domain.display.repository;
import com.d209.welight.domain.display.entity.DisplayLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikeRepository extends JpaRepository<DisplayLike, Long> {

    // Check if a user has already liked a display
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM DisplayLike l WHERE l.displayUid = :displayId AND l.userUid = :userId")
    boolean existsByDisplayUidAndUserUid(@Param("displayId") Long displayId, @Param("userId") Long userId);

    // Delete a like
    void deleteByDisplayUidAndUserUid(Long displayId, Long userId);
}
