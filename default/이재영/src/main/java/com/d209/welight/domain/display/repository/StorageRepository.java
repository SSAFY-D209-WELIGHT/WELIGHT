package com.d209.welight.domain.display.repository;
import com.d209.welight.domain.display.entity.DisplayStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StorageRepository extends JpaRepository<DisplayStorage, Long> {

    // Find all storage entries by user ID
    List<DisplayStorage> findByUserUid(Long userUid);

    // Check if a specific display is marked as favorite by a user
    boolean existsByUserUidAndDisplayUidAndIsFavoritesTrue(Long userUid, Long displayUid);

    // Find all favorite displays for a user
    List<DisplayStorage> findByUserUidAndIsFavoritesTrue(Long userUid);
}
