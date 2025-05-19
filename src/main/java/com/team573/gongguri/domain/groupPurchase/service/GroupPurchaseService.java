package com.team573.gongguri.domain.groupPurchase.service;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.service.ChatService;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseRequestDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseResponseDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseSimpleResponseDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseWithChatResponseDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseWithParticipantCountDto;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchaseParticipant;
import com.team573.gongguri.domain.groupPurchase.entity.ParticipationStatus;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.groupPurchase.entity.PurchaseFilter;
import com.team573.gongguri.domain.groupPurchase.mapper.GroupPurchaseMapper;
import com.team573.gongguri.domain.groupPurchase.mapper.GroupPurchaseParticipantMapper;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseJpqlRepository;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseParticipantRepository;
import com.team573.gongguri.domain.groupPurchase.repository.GroupPurchaseRepository;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.global.exception.CustomErrorCode;
import com.team573.gongguri.global.exception.CustomException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupPurchaseService {
    private final GroupPurchaseRepository groupPurchaseRepository;
    private final GroupPurchaseParticipantRepository participantRepository;
    private final MemberRepository memberRepository;
    private final ChatService chatService;
    private final GroupPurchaseJpqlRepository groupPurchaseJpqlRepository;
    private final GroupPurchaseParticipantRepository groupPurchaseParticipantRepository;

    @Transactional
    public GroupPurchaseResponseDto add(GroupPurchaseRequestDto dto, String email) {
        Member writer = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MEMBER));

        Univ univ = writer.getUniv();
        ChatRoom chatRoom;
        try {
            chatRoom = chatService.addChatRoom(email);
        } catch (Exception e) {
            log.error("채팅방 생성 실패", e);
            throw new CustomException(CustomErrorCode.CREATE_FAILED_GROUP_PURCHASE);
        }

        GroupPurchase groupPurchase;
        try {
            groupPurchase = GroupPurchaseMapper.toEntity(dto, writer, chatRoom, univ);
            groupPurchase.setImageUrl(dto.imageUrl());
            groupPurchaseRepository.save(groupPurchase);
        } catch (Exception e) {
            log.error("공동구매 게시글 저장 실패", e);
            throw new CustomException(CustomErrorCode.CREATE_FAILED_GROUP_PURCHASE);
        }

        try {
            GroupPurchaseParticipant participant = GroupPurchaseParticipantMapper.toEntity(groupPurchase, writer);
            participantRepository.save(participant);
        } catch (Exception e) {
            log.error("작성자를 참여자로 등록 실패", e);
            throw new CustomException(CustomErrorCode.JOIN_FAILED);
        }

        int currentParticipants = participantRepository.countByGroupPurchase_GroupId(groupPurchase.getGroupId());
        boolean isParticipated = true;

        return GroupPurchaseMapper.toDto(groupPurchase, currentParticipants, isParticipated);
    }

    @Transactional(readOnly = true)
    public GroupPurchaseResponseDto get(Long id, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MEMBER));
        GroupPurchase groupPurchase = groupPurchaseRepository.findById(id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_GROUP_PURCHASE));
        int currentParticipants = participantRepository.countByGroupPurchase_GroupId(id);
        boolean isParticipated = participantRepository.existsByGroupPurchase_GroupIdAndMember_Email(id, email);

        return GroupPurchaseMapper.toDto(groupPurchase, currentParticipants, isParticipated);
    }

    @Deprecated
    @Transactional(readOnly = true)
    public List<GroupPurchaseResponseDto> getAll(String email) {
        return groupPurchaseRepository.findAllActive().stream()
                .map(entity -> {
                    int currentParticipants = participantRepository.countByGroupPurchase_GroupId(entity.getGroupId());
                    boolean isParticipated = participantRepository.existsByGroupPurchase_GroupIdAndMember_Email(entity.getGroupId(), email);
                    return GroupPurchaseMapper.toDto(entity, currentParticipants, isParticipated);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GroupPurchaseResponseDto> getAllByCursor(
            Long cursorId,
            List<ProgressStatus> statuses,
            int size,
            String email
    ) {
        List<GroupPurchaseWithParticipantCountDto> groupPurchases =
                groupPurchaseJpqlRepository.findAllWithCursorAndParticipantCount(cursorId, statuses, size);

        return groupPurchases.stream()
                .map(dto -> {

                    return GroupPurchaseMapper.toDto(dto, null); // ← dto 기반 변환 메서드 필요
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupPurchaseResponseDto update(Long id, GroupPurchaseRequestDto dto) {

        GroupPurchase groupPurchase = groupPurchaseRepository.findByGroupIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_GROUP_PURCHASE));
        try {
            groupPurchase.update(
                    dto.title(),
                    dto.content(),
                    dto.price(),
                    dto.maxParticipants(),
                    dto.bank(),
                    dto.account(),
                    ProgressStatus.valueOf(dto.progressStatus().toUpperCase())
            );
            groupPurchase.setImageUrl(dto.imageUrl());
        } catch (Exception e) {
            log.error("공동구매 수정 실패", e);
            throw new CustomException(CustomErrorCode.UPDATE_FAILED_GROUP_PURCHASE);
        }
        return GroupPurchaseMapper.toDto(groupPurchase);
    }

    @Transactional
    public void delete(Long id) {
        GroupPurchase groupPurchase = groupPurchaseRepository.findByGroupIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_GROUP_PURCHASE));
        groupPurchase.markAsDeleted();
    }

    @Transactional
    public void join(Long groupId, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MEMBER));

        GroupPurchase groupPurchase = groupPurchaseRepository.findByGroupIdAndIsDeletedFalse(groupId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_GROUP_PURCHASE));

        int currentCount = participantRepository.countByGroupPurchase_GroupId(groupId);
        if (currentCount >= groupPurchase.getMaxParticipants()) {
            throw new CustomException(CustomErrorCode.PARTICIPANT_LIMIT_REACHED);
        }

        boolean alreadyJoined = participantRepository.existsByGroupPurchase_GroupIdAndMember_Email(groupId, email);
        if (alreadyJoined) {
            throw new CustomException(CustomErrorCode.ALREADY_JOINED);
        }

        try {
            GroupPurchaseParticipant participant = GroupPurchaseParticipantMapper.toEntity(groupPurchase, member);
            participantRepository.save(participant);
            chatService.addChatParticipation(groupPurchase.getChatRoom().getChatRoomId(), email);
        } catch (Exception e) {
            log.error("참여자 등록 실패", e);
            throw new CustomException(CustomErrorCode.JOIN_FAILED);
        }

        int afterJoinCount = currentCount + 1;
        if (afterJoinCount >= groupPurchase.getMaxParticipants()) {
            groupPurchase.setProgressStatus(ProgressStatus.CLOSED);
        }
    }

    @Transactional(readOnly = true)
    public List<GroupPurchaseWithChatResponseDto> getWithMessage(
        Integer size,
        Long cursorId,
        PurchaseFilter purchaseFilter,
        Long memberId
    ) {
        // 필터로 공동 구매 상태 구하기
        List<ProgressStatus> statuses = getStatusesByFilter(purchaseFilter);

        PageRequest pageable = PageRequest.of(0, size);

        // 공동 구매 조회
        List<GroupPurchase> groupPurchases
            = groupPurchaseRepository.findWithCursorAndParticipantCount(cursorId, memberId, statuses, pageable);

        // 맵으로 채팅 메시지 가져오기
        Map<Long, String> firstMessages = getFirstMessages(groupPurchases);

        return groupPurchases.stream()
            .map(groupPurchase -> GroupPurchaseMapper.toDtoWithMessage(
                groupPurchase,
                countParticipantsByStatus(groupPurchase, ParticipationStatus.JOINED),
                firstMessages
                )
            )
            .toList();
    }

    // 상태 조건 분리
    private List<ProgressStatus> getStatusesByFilter(PurchaseFilter filter) {
        return switch (filter) {
            case ONGOING -> List.of(ProgressStatus.RECRUITING, ProgressStatus.CLOSED);
            case COMPLETED -> List.of(ProgressStatus.COMPLETED);
            default -> List.of(ProgressStatus.RECRUITING, ProgressStatus.CLOSED, ProgressStatus.COMPLETED);
        };
    }

    // 조회한 공동 구매 채팅 메시지 조회
    private Map<Long, String> getFirstMessages(List<GroupPurchase> groupPurchases) {
        List<Long> chatRoomIds = groupPurchases.stream()
            .map(groupPurchase -> groupPurchase.getChatRoom().getChatRoomId())
            .toList();
        return chatService.getFirstMessageMap(chatRoomIds);
    }

    // ParticipationStatus 로 해당 공동 구매 참여자 수 조회
    private Long countParticipantsByStatus(GroupPurchase groupPurchase, ParticipationStatus status) {
        return groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(
            groupPurchase,
            status
        );
    }

    @Transactional(readOnly = true)
    public GroupPurchaseSimpleResponseDto getSimpleInfo(Long groupPurchaseId) {
        GroupPurchase groupPurchase = groupPurchaseRepository.findById(groupPurchaseId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_GROUP_PURCHASE));

        Long participantCount
            = groupPurchaseParticipantRepository.countByGroupPurchaseAndParticipationStatus(groupPurchase, ParticipationStatus.JOINED);

        return GroupPurchaseMapper.toDtoWithCount(groupPurchase, participantCount);
    }

    //특정 멤버가 작성한 공동구매글 조회
    public List<GroupPurchaseResponseDto> findCreatedPurchases(Long memberId, PurchaseFilter purchaseFilter){

        List<GroupPurchase> purchases;
        switch (purchaseFilter) {
            case ONGOING -> {
                List<ProgressStatus> statuses = List.of(ProgressStatus.RECRUITING, ProgressStatus.CLOSED);
                purchases = groupPurchaseRepository.findByMember_MemberIdAndProgressStatusIn(memberId, statuses);
            }
            case COMPLETED -> {
                purchases = groupPurchaseRepository.findByMember_MemberIdAndProgressStatus(memberId, ProgressStatus.COMPLETED);
            }
            default -> {
                purchases = groupPurchaseRepository.findByMember_MemberId(memberId);
            }
        }

        return purchases.stream()
                .map(purchase -> {
                    int currentParticipants = participantRepository.countByGroupPurchase_GroupId(purchase.getGroupId());
                    return GroupPurchaseMapper.toDto(purchase, currentParticipants, false);
                })
                .toList();
    }

}
