package com.team573.gongguri.domain.groupPurchase.repository;

import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchaseParticipant;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GroupPurchaseParticipantRepository extends JpaRepository<GroupPurchaseParticipant, Long> {
    int countByGroupPurchase_GroupId(Long groupId);
    boolean existsByGroupPurchase_GroupIdAndMember_Email(Long groupId, String email);
    List<GroupPurchaseParticipant> findByMember_MemberIdAndGroupPurchase_ProgressStatus(Long memberId, ProgressStatus progressStatus);
    Optional<GroupPurchaseParticipant> findByMember_memberIdAndGroupPurchase_GroupId(Long memberId, Long groupId);


    @Query("""
        SELECT g
        FROM GroupPurchaseParticipant g
        JOIN FETCH g.member
        WHERE g.groupPurchase.groupId = :groupPurchaseId
          AND (:cursorId IS NULL OR g.groupParticipantId < :cursorId)
          AND (:deposit IS NULL OR g.deposit = :deposit)
          AND (g.participationStatus = 'JOINED')
        ORDER BY g.groupParticipantId DESC
        """)
    List<GroupPurchaseParticipant> findParticipantsByCursor(
        @Param("groupPurchaseId") Long groupPurchaseId,
        @Param("cursorId") Long cursorParticipantId,
        @Param("deposit") Boolean deposit,
        Pageable pageable
    );
}
