package com.team573.gongguri.domain.groupPurchase.service;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
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
    private final GroupPurchaseRepository repository;
    private final GroupPurchaseParticipantRepository participantRepository;

    @Transactional
    public GroupPurchaseResponseDto add(GroupPurchaseRequestDto dto, Member writer, ChatRoom chatRoom, Univ univ) {
        try {
            GroupPurchase entity = GroupPurchaseMapper.toEntity(dto, writer, chatRoom, univ);
            entity.setImageUrl(dto.imageUrl()); // 이미지 URL 직접 설정
            repository.save(entity);

            GroupPurchaseParticipant participant = GroupPurchaseParticipant.builder()
                    .groupPurchase(entity)
                    .member(writer)
                    .participationStatus(ParticipationStatus.JOINED)
                    .build();
            participantRepository.save(participant);

            int currentParticipants = participantRepository.countByGroupPurchase_GroupId(entity.getGroupId());
            boolean isParticipated = true; // 어차피 작성자이므로 true로 고정해도 무방

            return GroupPurchaseMapper.toDto(entity, currentParticipants, isParticipated);
        } catch (Exception e) {
            throw new ErrorException(ErrorCode.CREATE_FAILED_GROUP_PURCHASE);
        }
    }

    @Transactional(readOnly = true)
    public GroupPurchaseResponseDto get(Long id) {
        GroupPurchase entity = repository.findById(id)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));
        int currentParticipants = participantRepository.countByGroupPurchase_GroupId(id);
        boolean isParticipated = participantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(id, 3L); // 로그인 대체
        return GroupPurchaseMapper.toDto(entity, currentParticipants, isParticipated);
    }

    @Transactional(readOnly = true)
    public List<GroupPurchaseResponseDto> getAll(Member member) {
        return repository.findAll().stream()
                .map(entity -> {
                    int currentParticipants = participantRepository.countByGroupPurchase_GroupId(entity.getGroupId());
                    boolean isParticipated = participantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(entity.getGroupId(), member.getMemberId());
                    return GroupPurchaseMapper.toDto(entity, currentParticipants, isParticipated);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupPurchaseResponseDto update(Long id, GroupPurchaseRequestDto dto) {
        try {
            GroupPurchase entity = repository.findById(id)
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
            entity.setImageUrl(dto.imageUrl()); // 업데이트 시에도 imageUrl 반영

            return GroupPurchaseMapper.toDto(entity);
        } catch (Exception e) {
            throw new ErrorException(ErrorCode.UPDATE_FAILED_GROUP_PURCHASE);
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            GroupPurchase entity = repository.findById(id)
                    .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));
            repository.delete(entity);
        } catch (Exception e) {
            throw new ErrorException(ErrorCode.DELETE_FAILED_GROUP_PURCHASE);
        }
    }

    @Transactional
    public void join(Long groupId, Member member) {
        try {
            if (member == null) {
                log.error("[JOIN ERROR] Member is null");
                throw new ErrorException(ErrorCode.NOT_FOUND_MEMBER);
            }

            log.info("[JOIN] START: groupId={}, memberId={}", groupId, member.getMemberId());

            GroupPurchase groupPurchase = repository.findById(groupId)
                    .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_GROUP_PURCHASE));

            boolean alreadyJoined = participantRepository.existsByGroupPurchase_GroupIdAndMember_MemberId(groupId, member.getMemberId());
            if (alreadyJoined) {
                throw new ErrorException(ErrorCode.ALREADY_JOINED);
            }

            GroupPurchaseParticipant participant = GroupPurchaseParticipant.builder()
                    .groupPurchase(groupPurchase)
                    .member(member)
                    .participationStatus(ParticipationStatus.JOINED)
                    .build();

            participantRepository.save(participant);

            log.info("[JOIN] groupId: {}, memberId: {}", groupId, member.getMemberId());
        } catch (Exception e) {
            log.error("[JOIN ERROR] message: {}", e.getMessage(), e);
            throw new ErrorException(ErrorCode.JOIN_FAILED);    // 신규 에러코드가 없다면 하나 추가
        }
    }

}
