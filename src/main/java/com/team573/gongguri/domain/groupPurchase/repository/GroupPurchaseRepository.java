package com.team573.gongguri.domain.groupPurchase.repository;

import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupPurchaseRepository extends JpaRepository<GroupPurchase, Long> {
    List<GroupPurchase> findByMember_MemberId(Long memberId);

    List<GroupPurchase> findByMember_MemberIdAndProgressStatusIn(Long memberId, List<ProgressStatus> recruiting);

    // softDelete 단건 조회
    Optional<GroupPurchase> findByGroupIdAndIsDeletedFalse(Long groupId);

    Boolean existsByGroupIdAndMember_MemberId(Long groupId, Long memberId);
}
