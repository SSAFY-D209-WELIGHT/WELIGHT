package com.d209.welight.domain.cheer.repository;

import com.d209.welight.domain.cheer.entity.Cheerroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CheerroomRepository extends JpaRepository<Cheerroom, Long> {
    List<Cheerroom> findByIsDoneFalse();
    List<Cheerroom> findByLatitudeBetweenAndLongitudeBetween(
            BigDecimal latStart, BigDecimal latEnd,
            BigDecimal longStart, BigDecimal longEnd
    );
}