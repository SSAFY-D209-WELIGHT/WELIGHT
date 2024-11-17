package com.d209.welight.domain.cheer.repository;

import com.d209.welight.domain.cheer.entity.Cheerroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheerroomRepository extends JpaRepository<Cheerroom, Long> {

    @Query("SELECT DISTINCT c FROM Cheerroom c " +
            "WHERE c.isDone = false AND " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(c.latitude)) * " +
            "cos(radians(c.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(c.latitude)))) <= :upToKm")
    List<Cheerroom> findByGeo(@Param("latitude") Double latitude,
                             @Param("longitude") Double longitude,
                             @Param("upToKm") Double upToKm);

    Optional<Cheerroom> findByNumber(Long cheerroomNumber);

    Optional<Cheerroom> findByNumberAndIsDoneFalse(Long cheeroomNumber);
}