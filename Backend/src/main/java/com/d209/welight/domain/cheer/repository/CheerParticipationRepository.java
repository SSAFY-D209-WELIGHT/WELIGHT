package com.d209.welight.domain.cheer.repository;

import com.d209.welight.domain.cheer.entity.CheerParticipation;
import com.d209.welight.domain.cheer.entity.CheerParticipationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheerParticipationRepository extends JpaRepository<CheerParticipation, CheerParticipationId> {
    List<CheerParticipation> findByUser_UserUid(Long userUid);
    List<CheerParticipation> findByCheerroom_Id(Long cheerroomUid);
    Optional<CheerParticipation> findByUser_UserUidAndCheerroom_Id(Long userUid, Long cheerroomUid);
}
