package com.team573.gongguri.domain.groupPurchase.service;

import static com.team573.gongguri.global.exception.CustomErrorCode.CANNOT_CANCEL_PAID_PARTICIPANT;
import static com.team573.gongguri.global.exception.CustomErrorCode.NOT_FOUND_PARTICIPANT;
import static com.team573.gongguri.global.exception.CustomErrorCode.UNAUTHORIZED_GROUP_PURCHASE_MANAGE;

import com.team573.gongguri.domain.chat.service.ChatService;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseParticipantResponseDto;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchaseParticipant;
import com.team573.gongguri.domain.groupPurchase.mapper.GroupPurchaseParticipantMapper;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseParticipantRepository;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.team573.gongguri.global.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupPurchaseParticipantService {

    private final GroupPurchaseParticipantRepository groupPurchaseParticipantRepository;
    private final GroupPurchaseRepository groupPurchaseRepository;
    private final ChatService chatService;

    @Transactional
    public void cancelParticipation(Long groupPurchaseId, Long participantId, Long memberId) {
        GroupPurchaseParticipant participant
            = this.getParticipantToManaged(groupPurchaseId, participantId, memberId);

        // 이미 결제를 했다면 예외 처리
        if (participant.getDeposit()) {
            throw new CustomException(CANNOT_CANCEL_PAID_PARTICIPANT);
        }

        chatService.deleteChatParticipation(groupPurchaseId, memberId);
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

    public List<GroupPurchaseParticipantResponseDto> getParticipants(
        Long groupPurchaseId,
        Long cursorParticipantId,
        Boolean deposit,
        Long memberId,
        int size) {

        PageRequest pageRequest = PageRequest.of(0, size);
        List<GroupPurchaseParticipant> participants = groupPurchaseParticipantRepository.findParticipantsByCursor(
            groupPurchaseId, cursorParticipantId, deposit, memberId, pageRequest);

        return participants.stream()
            .map(GroupPurchaseParticipantMapper::toDto)
            .toList();
    }

    private GroupPurchaseParticipant getParticipantToManaged(Long groupPurchaseId,
        Long participantId, Long memberId) {
        // member가 공동 구매 관리자 인지 확인
        if (!groupPurchaseRepository.existsByGroupIdAndMember_MemberId(groupPurchaseId, memberId)) {
            throw new CustomException(UNAUTHORIZED_GROUP_PURCHASE_MANAGE);
        }

        // 관리하기 위한 참여자 조회
        return groupPurchaseParticipantRepository.findById(participantId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_PARTICIPANT));
    }
}
