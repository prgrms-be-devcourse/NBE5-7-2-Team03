package com.team573.gongguri.domain.groupPurchase.repository;

import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchaseParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupPurchaseParticipantRepository extends JpaRepository<GroupPurchaseParticipant, Long> {
   int countByGroupPurchase_GroupId(Long groupId);
   boolean existsByGroupPurchase_GroupIdAndMember_MemberId(Long groupId, Long memberId);
}
