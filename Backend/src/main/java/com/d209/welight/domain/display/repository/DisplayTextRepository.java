package com.d209.welight.domain.display.repository;

import com.d209.welight.domain.display.entity.Display;
import com.d209.welight.domain.display.entity.DisplayText;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisplayTextRepository extends JpaRepository<DisplayText, Long> {

    List<DisplayText> findByDisplay(Display display);

    void deleteByDisplay(Display display);

//    @Query("SELECT dt.displayTextDetail FROM DisplayText dt WHERE dt.display.displayUid = :displayUid")
//    List<String> findTextDetailsByDisplayUid(@Param("displayUid") Long displayUid);
}
