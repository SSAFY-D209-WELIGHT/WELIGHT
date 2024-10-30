package com.d209.welight.domain.display.repository;

import com.d209.welight.domain.display.entity.DisplayTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisplayTagRepository extends JpaRepository<DisplayTag, Long> {
}
