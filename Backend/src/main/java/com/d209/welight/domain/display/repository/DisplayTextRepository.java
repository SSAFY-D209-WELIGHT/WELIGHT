package com.d209.welight.domain.display.repository;

import com.d209.welight.domain.display.entity.Display;
import com.d209.welight.domain.display.entity.DisplayText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisplayTextRepository extends JpaRepository<DisplayText, Long> {

    List<DisplayText> findByDisplay(Display display);
}
