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
        ChatRoom chatRoom = chatService.addChatRoom(email);

        try {
            GroupPurchase entity = GroupPurchaseMapper.toEntity(dto, writer, chatRoom, univ);
            entity.setImageUrl(dto.imageUrl());
            groupPurchaseRepository.save(entity);

            GroupPurchaseParticipant participant = GroupPurchaseParticipant.builder()
                    .groupPurchase(entity)
                    .member(writer)
                    .participationStatus(ParticipationStatus.JOINED)
                    .build();
            participantRepository.save(participant);

            int currentParticipants = participantRepository.countByGroupPurchase_GroupId(entity.getGroupId());
            boolean isParticipated = true;
            return GroupPurchaseMapper.toDto(entity, currentParticipants, isParticipated);
        } catch (Exception e) {
            throw new ErrorException(ErrorCode.CREATE_FAILED_GROUP_PURCHASE);
        }
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
        try {
            GroupPurchase entity = groupPurchaseRepository.findById(id)
                    .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));

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

            return GroupPurchaseMapper.toDto(entity);
        } catch (Exception e) {
            throw new ErrorException(ErrorCode.UPDATE_FAILED_GROUP_PURCHASE);
        }
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

        GroupPurchaseParticipant participant = GroupPurchaseParticipant.builder()
                .groupPurchase(groupPurchase)
                .member(member)
                .participationStatus(ParticipationStatus.JOINED)
                .build();

        participantRepository.save(participant);
        chatService.addChatParticipation(groupPurchase.getChatRoom().getChatRoomId(), email);

        int afterJoinCount = currentCount + 1;
        if (afterJoinCount >= groupPurchase.getMaxParticipants()) {
            groupPurchase.setProgressStatus(ProgressStatus.CLOSED);
        }
    }
}
