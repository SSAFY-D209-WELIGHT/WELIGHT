package com.d209.welight.domain.display.repository;

import com.d209.welight.domain.display.entity.DisplayBackground;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface DisplayBackgroundRepository extends JpaRepository<DisplayBackground, Long> {
}
