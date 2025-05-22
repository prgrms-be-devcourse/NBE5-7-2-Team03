package com.team573.gongguri.domain.grouppurchase.repository;

import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.grouppurchase.entity.GroupPurchaseParticipant;
import com.team573.gongguri.domain.grouppurchase.entity.ParticipationStatus;
import com.team573.gongguri.domain.grouppurchase.entity.ProgressStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupPurchaseParticipantRepository extends JpaRepository<GroupPurchaseParticipant, Long> {

    boolean existsByGroupPurchase_GroupIdAndMember_MemberId(Long groupPurchaseGroupId, Long memberId);

    boolean existsByGroupPurchase_GroupIdAndDepositIsTrue(Long groupId);

    List<GroupPurchaseParticipant> findByMember_MemberIdAndGroupPurchase_ProgressStatus(
        Long memberId, ProgressStatus progressStatus);

    @Query("""
        SELECT g
        FROM GroupPurchaseParticipant g
        JOIN FETCH g.member
        WHERE g.groupPurchase.groupId = :groupPurchaseId
          AND (:cursorId IS NULL OR g.groupParticipantId < :cursorId)
          AND (:deposit IS NULL OR g.deposit = :deposit)
          AND (g.participationStatus = 'JOINED')
          AND (g.member.memberId != :memberId)
        ORDER BY g.groupParticipantId DESC
        """)
    List<GroupPurchaseParticipant> findParticipantsByCursor(
        @Param("groupPurchaseId") Long groupPurchaseId,
        @Param("cursorId") Long cursorParticipantId,
        @Param("deposit") Boolean deposit,
        @Param("memberId") Long memberId,
        Pageable pageable
    );

    @Query("""
    SELECT gpp
    FROM GroupPurchaseParticipant gpp
    JOIN FETCH gpp.groupPurchase gp
    JOIN FETCH gp.chatRoom cr
    WHERE gpp.member.memberId = :memberId
      AND gpp.participationStatus = 'JOINED'
      AND (:cursorId IS NULL OR gpp.groupParticipantId < :cursorId)
      AND gp.progressStatus IN :statuses
      AND gp.deleted = false
    ORDER BY gpp.groupParticipantId DESC
""")
    List<GroupPurchaseParticipant> findByMemberWithCursor(
        Long cursorId,
        Long memberId,
        List<ProgressStatus> statuses,
        Pageable pageable
    );

    Long countByGroupPurchaseAndParticipationStatus(
        GroupPurchase groupPurchase,
        ParticipationStatus participationStatus
    );
}
