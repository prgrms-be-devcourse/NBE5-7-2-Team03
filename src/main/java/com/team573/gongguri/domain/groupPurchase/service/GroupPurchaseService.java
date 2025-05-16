package com.team573.gongguri.domain.groupPurchase.service;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.service.ChatService;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseRequestDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseResponseDto;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchaseParticipant;
import com.team573.gongguri.domain.groupPurchase.entity.ParticipationStatus;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.groupPurchase.mapper.GroupPurchaseMapper;
import com.team573.gongguri.domain.groupPurchase.mapper.GroupPurchaseParticipantMapper;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseParticipantRepository;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.global.exception.ErrorCode;
import com.team573.gongguri.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupPurchaseService {
    private final GroupPurchaseRepository groupPurchaseRepository;
    private final GroupPurchaseParticipantRepository participantRepository;
    private final MemberRepository memberRepository;
    private final ChatService chatService;

    @Transactional
    public GroupPurchaseResponseDto add(GroupPurchaseRequestDto dto, String email) {
        Member writer = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));

        Univ univ = writer.getUniv();
        ChatRoom chatRoom;
        try {
            chatRoom = chatService.addChatRoom(email);
        } catch (Exception e) {
            log.error("채팅방 생성 실패", e);
            throw new ErrorException(ErrorCode.CREATE_FAILED_GROUP_PURCHASE);
        }

        GroupPurchase groupPurchase;
        try {
            groupPurchase = GroupPurchaseMapper.toEntity(dto, writer, chatRoom, univ);
            groupPurchase.setImageUrl(dto.imageUrl());
            groupPurchaseRepository.save(groupPurchase);
        } catch (Exception e) {
            log.error("공동구매 게시글 저장 실패", e);
            throw new ErrorException(ErrorCode.CREATE_FAILED_GROUP_PURCHASE);
        }

        try {
            GroupPurchaseParticipant participant = GroupPurchaseParticipantMapper.toEntity(groupPurchase, writer);
            participantRepository.save(participant);
        } catch (Exception e) {
            log.error("작성자를 참여자로 등록 실패", e);
            throw new ErrorException(ErrorCode.JOIN_FAILED);
        }

        int currentParticipants = participantRepository.countByGroupPurchase_GroupId(groupPurchase.getGroupId());
        boolean isParticipated = true;

        return GroupPurchaseMapper.toDto(groupPurchase, currentParticipants, isParticipated);
    }

    @Transactional(readOnly = true)
    public GroupPurchaseResponseDto get(Long id, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));
        GroupPurchase entity = groupPurchaseRepository.findById(id)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));
        int currentParticipants = participantRepository.countByGroupPurchase_GroupId(id);
        boolean isParticipated = participantRepository.existsByGroupPurchase_GroupIdAndMember_Email(id, email);
        return GroupPurchaseMapper.toDto(entity, currentParticipants, isParticipated);
    }

    @Transactional(readOnly = true)
    public List<GroupPurchaseResponseDto> getAll(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));
        return groupPurchaseRepository.findAll().stream()
                .map(entity -> {
                    int currentParticipants = participantRepository.countByGroupPurchase_GroupId(entity.getGroupId());
                    boolean isParticipated = participantRepository.existsByGroupPurchase_GroupIdAndMember_Email(entity.getGroupId(), email);
                    return GroupPurchaseMapper.toDto(entity, currentParticipants, isParticipated);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupPurchaseResponseDto update(Long id, GroupPurchaseRequestDto dto) {

        GroupPurchase entity = groupPurchaseRepository.findById(id)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));
        try {
            entity.update(
                    dto.title(),
                    dto.content(),
                    dto.price(),
                    dto.maxParticipants(),
                    dto.bank(),
                    dto.account(),
                    ProgressStatus.valueOf(dto.progressStatus().toUpperCase())
            );
            entity.setImageUrl(dto.imageUrl());
        } catch (Exception e) {
            log.error("공동구매 수정 실패", e);
            throw new ErrorException(ErrorCode.UPDATE_FAILED_GROUP_PURCHASE);
        }
        return GroupPurchaseMapper.toDto(entity);
    }

    @Transactional
    public void delete(Long id) {
        GroupPurchase entity = groupPurchaseRepository.findById(id)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));
        groupPurchaseRepository.delete(entity);
    }

    @Transactional
    public void join(Long groupId, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));

        GroupPurchase groupPurchase = groupPurchaseRepository.findById(groupId)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));

        int currentCount = participantRepository.countByGroupPurchase_GroupId(groupId);
        if (currentCount >= groupPurchase.getMaxParticipants()) {
            throw new ErrorException(ErrorCode.PARTICIPANT_LIMIT_REACHED);
        }

        boolean alreadyJoined = participantRepository.existsByGroupPurchase_GroupIdAndMember_Email(groupId, email);
        if (alreadyJoined) {
            throw new ErrorException(ErrorCode.ALREADY_JOINED);
        }

        try {
            GroupPurchaseParticipant participant = GroupPurchaseParticipantMapper.toEntity(groupPurchase, member);
            participantRepository.save(participant);
            chatService.addChatParticipation(groupPurchase.getChatRoom().getChatRoomId(), email);
        } catch (Exception e) {
            log.error("참여자 등록 실패", e);
            throw new ErrorException(ErrorCode.JOIN_FAILED);
        }

        int afterJoinCount = currentCount + 1;
        if (afterJoinCount >= groupPurchase.getMaxParticipants()) {
            groupPurchase.setProgressStatus(ProgressStatus.CLOSED);
        }
    }
}
