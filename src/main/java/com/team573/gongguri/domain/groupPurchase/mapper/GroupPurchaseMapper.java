package com.team573.gongguri.domain.groupPurchase.mapper;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseSimpleResponseDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseWithChatResponseDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseRequestDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseResponseDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseWithParticipantCountDto;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import java.util.Map;

public class GroupPurchaseMapper {
    public static GroupPurchase toEntity(GroupPurchaseRequestDto dto, Member writer, ChatRoom chatRoom, Univ univ) {
        return GroupPurchase.builder()
                .member(writer)
                .chatRoom(chatRoom)
                .univ(univ)
                .title(dto.title())
                .content(dto.content())
                .price(dto.price())
                .maxParticipants(dto.maxParticipants())
                .bank(dto.bank())
                .account(dto.account())
                .imageUrl(dto.imageUrl()) // 추가!
                .progressStatus(ProgressStatus.RECRUITING)
                .build();
    }

    public static GroupPurchaseResponseDto toDto(GroupPurchase entity) {
        return toDto(entity, 0, false);
    }

    public static GroupPurchaseResponseDto toDto(GroupPurchase entity, int currentParticipants, boolean isParticipated) {
        return new GroupPurchaseResponseDto(
                entity.getGroupId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getPrice(),
                entity.getMaxParticipants(),
                currentParticipants,
                entity.getBank(),
                entity.getAccount(),
                entity.getProgressStatus().toString(),
                entity.getImageUrl(),
                isParticipated,
                entity.getMember().getEmail(),
                entity.getMember().getNickname(),
                entity.getMember().getMemberId()

        );
    }

    public static GroupPurchaseWithChatResponseDto toDtoWithMessage(
        GroupPurchase groupPurchase,
        Long participantCount,
        Map<Long, String> firstMessages
    ) {
        return GroupPurchaseWithChatResponseDto.builder()
            .id(groupPurchase.getGroupId())
            .title(groupPurchase.getTitle())
            .maxParticipants(groupPurchase.getMaxParticipants())
            .progressStatus(groupPurchase.getProgressStatus().toString())
            .imageUrl(groupPurchase.getImageUrl())
            .chatMessage(firstMessages.get(groupPurchase.getChatRoom().getChatRoomId()))
            .participantCount(participantCount)
            .createAt(groupPurchase.getCreatedAt())
            .build();
    }

    public static GroupPurchaseResponseDto toDto(GroupPurchaseWithParticipantCountDto dto, Boolean isParticipated) {
        return GroupPurchaseResponseDto.builder()
                .id(dto.groupId())
                .title(dto.title())
                .content(dto.content())
                .price(dto.price())
                .maxParticipants(dto.maxParticipants())
                .currentParticipants(dto.participantCount().intValue())
                .progressStatus(dto.progressStatus().toString())
                .imageUrl(dto.imageUrl())
                .isParticipated(isParticipated)
                .build();
    }

    public static GroupPurchaseSimpleResponseDto toDtoWithCount(GroupPurchase groupPurchase, Long participantCount) {
        return GroupPurchaseSimpleResponseDto.builder()
            .id(groupPurchase.getGroupId())
            .title(groupPurchase.getTitle())
            .maxParticipants(groupPurchase.getMaxParticipants())
            .participantCount(participantCount)
            .progressStatus(groupPurchase.getProgressStatus())
            .imageUrl(groupPurchase.getImageUrl())
            .price(groupPurchase.getPrice())
            .build();
    }

}

