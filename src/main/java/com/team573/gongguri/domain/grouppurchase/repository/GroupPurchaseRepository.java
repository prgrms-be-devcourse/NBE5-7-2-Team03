package com.team573.gongguri.domain.grouppurchase.repository;

import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GroupPurchaseRepository extends JpaRepository<GroupPurchase, Long> {
    List<GroupPurchase> findByMember_MemberId(Long memberId);

    List<GroupPurchase> findByMember_MemberIdAndProgressStatusIn(Long memberId, List<ProgressStatus> recruiting);

    // softDelete 단건 조회
    Optional<GroupPurchase> findByGroupIdAndDeletedFalse(Long groupId);

    // softDelete 전체 목록
    @Query("SELECT g FROM GroupPurchase g WHERE g.deleted = false")
    List<GroupPurchase> findAllActive();

    Boolean existsByGroupIdAndMember_MemberId(Long groupId, Long memberId);

    @Query("""
        SELECT gp
        FROM GroupPurchase gp
        JOIN FETCH gp.chatRoom cr
        JOIN GroupPurchaseParticipant gpp 
            ON gpp.groupPurchase.groupId = gp.groupId
            AND gpp.member.memberId = :memberId
            AND gpp.participationStatus = 'JOINED'
        WHERE (:cursorId IS NULL OR gp.groupId < :cursorId)
        AND (gp.progressStatus IN :statuses)
        AND (gp.deleted = false)
        ORDER BY gp.groupId DESC
    """)
    List<GroupPurchase> findWithCursorAndParticipantCount(
        Long cursorId,
        Long memberId,
        List<ProgressStatus> statuses,
        Pageable pageable
    );
}
