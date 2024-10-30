package com.d209.welight.domain.display.repository;

import com.d209.welight.domain.display.entity.DisplayText;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisplayTextRepository extends JpaRepository<DisplayText, Long> {
}
