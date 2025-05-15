package com.team573.gongguri.domain.groupPurchase.repository;

import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GroupPurchaseRepository extends JpaRepository<GroupPurchase, Long> {
    List<GroupPurchase> findByMember_MemberIdAndProgressStatus(Long memberId, ProgressStatus status);
    List<GroupPurchase> findByMember_MemberId(Long memberId);

    // softDelete 단건 조회
    Optional<GroupPurchase> findByGroupIdAndIsDeletedFalse(Long groupId);

    // softDelete 전체 목록
    @Query("SELECT g FROM GroupPurchase g WHERE g.isDeleted = false")
    List<GroupPurchase> findAllActive();

}
