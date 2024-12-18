package com.d209.welight.domain.cheer.repository;

import com.d209.welight.domain.cheer.entity.Cheerroom;
import com.d209.welight.domain.cheer.entity.cheerparticipation.CheerParticipation;
import com.d209.welight.domain.cheer.entity.cheerparticipation.CheerParticipationId;
import com.d209.welight.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheerParticipationRepository extends JpaRepository<CheerParticipation, CheerParticipationId> {

    Optional<CheerParticipation> findByUserAndCheerroomId(User user, Long cheerRoomUid);
    Optional<CheerParticipation> findByUserAndCheerroomNumber(User user, Long cheerNumber);
    void deleteByUserAndCheerroom(User user, Cheerroom cheerRoom);

    List<CheerParticipation> findByCheerroomAndLastExitTimeIsNull(Cheerroom cheerroom);

    @Query("SELECT cp FROM CheerParticipation cp " +
            "JOIN FETCH cp.cheerroom c " +
            "LEFT JOIN FETCH c.displays cd " +
            "LEFT JOIN FETCH cd.display d " +
            "WHERE cp.user.userUid = :userUid " +
            "ORDER BY cp.lastExitTime DESC")
    List<CheerParticipation> findUserParticipationHistory(Long userUid);

    @Query("SELECT COUNT(cp) FROM CheerParticipation cp " +
            "WHERE cp.cheerroom.id = :cheerroomId")
    int countParticipantsByCheerroomId(Long cheerroomId);

    @Query("SELECT cp FROM CheerParticipation cp " +
            "JOIN FETCH cp.cheerroom c " +
            "LEFT JOIN FETCH c.displays cd " +
            "LEFT JOIN FETCH cd.display " +
            "WHERE cp.user.userUid = :userUid " +
            "AND cp.cheerroom.id = :cheerroomUid")
    Optional<CheerParticipation> findByUserAndCheerroom(
            Long userUid, Long cheerroomUid);

    @Query("SELECT COUNT(cp) FROM CheerParticipation cp " +
            "WHERE cp.cheerroom.id = :cheerroomId AND cp.lastExitTime IS NULL")
    int countActiveParticipantsByCheerroomId(@Param("cheerroomId") Long cheerroomId);
}
