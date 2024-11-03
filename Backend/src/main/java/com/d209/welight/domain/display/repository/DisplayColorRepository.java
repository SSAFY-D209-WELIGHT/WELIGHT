package com.d209.welight.domain.display.repository;

import com.d209.welight.domain.display.entity.DisplayBackground;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.d209.welight.domain.display.entity.DisplayColor;

import java.util.Optional;

@Repository
public interface DisplayColorRepository extends JpaRepository<DisplayColor, Long> {
    Optional<DisplayColor> findByDisplayBackground(DisplayBackground background);

    void deleteByDisplayBackground(DisplayBackground background);
}
