package com.d209.welight.domain.display.repository;

import com.d209.welight.domain.display.entity.Display;
import com.d209.welight.domain.display.entity.DisplayTag;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface DisplayTagRepository extends JpaRepository<DisplayTag, Long> {
    List<DisplayTag> findByDisplay(Display display);

    void deleteByDisplay(Display display);

//    Arrays findByDisplayUid(Long displayUid);

//    @Query("SELECT dt.displayTagText FROM DisplayTag dt WHERE dt.display.displayUid = :displayUid")
//    List<String> findTagTextsByDisplayUid(@Param("displayUid") Long displayUid);
}
