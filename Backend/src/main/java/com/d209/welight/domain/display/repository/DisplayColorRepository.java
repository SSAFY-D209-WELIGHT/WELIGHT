package com.d209.welight.domain.display.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.d209.welight.domain.display.entity.DisplayColor;

@Repository
public interface DisplayColorRepository extends JpaRepository<DisplayColor, Long> {
}
