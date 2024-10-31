package com.d209.welight.domain.display.repository;

import com.d209.welight.domain.display.entity.Display;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DisplayRepository extends JpaRepository<Display, Long> {
    
    // 게시 여부가 1인 디스플레이만 조회
    Page<Display> findAllByDisplayIsPostedTrue(Pageable pageable);
}
