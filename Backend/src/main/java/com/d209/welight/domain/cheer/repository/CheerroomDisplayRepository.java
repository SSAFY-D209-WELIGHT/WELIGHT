package com.d209.welight.domain.cheer.repository;

import com.d209.welight.domain.cheer.entity.Cheerroom;
import com.d209.welight.domain.cheer.entity.cheerroomdisplay.CheerroomDisplay;
import com.d209.welight.domain.display.entity.Display;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CheerroomDisplayRepository extends JpaRepository<CheerroomDisplay, Long> {
    
//    // 특정 응원방과 디스플레이로 CheerroomDisplay 찾기
//    Optional<CheerroomDisplay> findByCheerroomAndDisplay(Cheerroom cheerroom, Display display);
//
//    // 특정 응원방과 썸네일 URL로 CheerroomDisplay 찾기
//    Optional<CheerroomDisplay> findByCheerroomAndDisplayThumbnailUrl(Cheerroom cheerroom, String thumbnailUrl);
//
//    // 특정 응원방의 모든 CheerroomDisplay 삭제
//    void deleteAllByCheerroom(Cheerroom cheerroom);
} 