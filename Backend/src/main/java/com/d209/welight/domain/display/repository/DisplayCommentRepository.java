package com.d209.welight.domain.display.repository;

import com.d209.welight.domain.display.entity.Display;
import com.d209.welight.domain.display.entity.DisplayComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisplayCommentRepository extends JpaRepository<DisplayComment, Long> {
    List<DisplayComment> findByDisplayOrderByCommentCreatedAtDesc(Display display);
    List<DisplayComment> findByDisplayAndParentCommentIsNullOrderByCommentCreatedAtDesc(Display display);
    List<DisplayComment> findByDisplayAndParentCommentIsNullOrderByCommentCreatedAt(Display display);
    void deleteByCommentUid(Long commentUid);
}