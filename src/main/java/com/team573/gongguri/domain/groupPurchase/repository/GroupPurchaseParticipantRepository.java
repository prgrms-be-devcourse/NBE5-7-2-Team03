package com.team573.gongguri.domain.groupPurchase.repository;

import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchaseParticipant;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupPurchaseParticipantRepository extends JpaRepository<GroupPurchaseParticipant, Long> {
    int countByGroupPurchase_GroupId(Long groupId);
    boolean existsByGroupPurchase_GroupIdAndMember_Email(Long groupId, String email);
    List<GroupPurchaseParticipant> findByMember_MemberIdAndGroupPurchase_ProgressStatus(Long memberId, ProgressStatus progressStatus);
    Optional<GroupPurchaseParticipant> findByMember_memberIdAndGroupPurchase_GroupId(Long memberId, Long groupId);
}
