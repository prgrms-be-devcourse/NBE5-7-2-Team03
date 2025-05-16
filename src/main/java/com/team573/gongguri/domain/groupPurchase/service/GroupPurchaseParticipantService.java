package com.team573.gongguri.domain.groupPurchase.service;

import static com.team573.gongguri.global.exception.ErrorCode.CANNOT_CANCEL_PAID_PARTICIPANT;
import static com.team573.gongguri.global.exception.ErrorCode.NOT_FOUND_PARTICIPANT;
import static com.team573.gongguri.global.exception.ErrorCode.UNAUTHORIZED_GROUP_PURCHASE_MANAGE;

import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchaseParticipant;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseParticipantRepository;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.team573.gongguri.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupPurchaseParticipantService {
    private final GroupPurchaseParticipantRepository groupPurchaseParticipantRepository;
    private final GroupPurchaseRepository groupPurchaseRepository;

    @Transactional
    public void cancelParticipation(Long groupPurchaseId, Long participantId, Long memberId) {
        GroupPurchaseParticipant participant
            = this.getParticipantToManaged(groupPurchaseId, participantId, memberId);

        // 이미 결제를 했다면 예외 처리
        if (participant.getDeposit()) {
            throw new ErrorException(CANNOT_CANCEL_PAID_PARTICIPANT);
        }

        participant.cancelMember();
        groupPurchaseParticipantRepository.save(participant);
    }

    @Transactional
    public void confirmDeposit(Long groupPurchaseId, Long participantId, Long memberId) {
        GroupPurchaseParticipant participant
            = this.getParticipantToManaged(groupPurchaseId, participantId, memberId);

        participant.confirmDeposit();
        groupPurchaseParticipantRepository.save(participant);
    }

    private GroupPurchaseParticipant getParticipantToManaged(Long groupPurchaseId, Long participantId, Long memberId) {
        // member가 공동 구매 관리자 인지 확인
        if (!groupPurchaseRepository.existsByGroupIdAndMember_MemberId(groupPurchaseId, memberId)) {
            throw new ErrorException(UNAUTHORIZED_GROUP_PURCHASE_MANAGE);
        }

        // 관리하기 위한 참여자 조회
        return groupPurchaseParticipantRepository.findByMember_memberIdAndGroupPurchase_GroupId(participantId, groupPurchaseId)
            .orElseThrow(() -> new ErrorException(NOT_FOUND_PARTICIPANT));
    }
}
