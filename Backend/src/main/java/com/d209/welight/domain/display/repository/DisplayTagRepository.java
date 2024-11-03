package com.d209.welight.domain.display.repository;

import com.d209.welight.domain.display.entity.Display;
import com.d209.welight.domain.display.entity.DisplayTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisplayTagRepository extends JpaRepository<DisplayTag, Long> {
    List<DisplayTag> findByDisplay(Display display);

    void deleteByDisplay(Display display);
}
