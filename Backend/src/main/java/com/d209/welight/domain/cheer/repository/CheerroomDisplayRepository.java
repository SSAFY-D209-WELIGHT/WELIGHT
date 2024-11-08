package com.d209.welight.domain.cheer.repository;

import com.d209.welight.domain.cheer.entity.Cheerroom;
import com.d209.welight.domain.cheer.entity.cheerroomdisplay.CheerroomDisplay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheerroomDisplayRepository extends JpaRepository<CheerroomDisplay, Long> {
    List<CheerroomDisplay> findByCheerroom(Cheerroom cheerroom);
}