package com.team573.gongguri.domain.groupPurchase.mapper;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseRequestDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseResponseDto;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;

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
               .progressStatus(ProgressStatus.RECRUITING)
               .build();

    }

    public static GroupPurchaseResponseDto toDto(GroupPurchase entity) {
        return new GroupPurchaseResponseDto(
                entity.getGroupId(),
                entity.getTitle(),
                entity.getContent(),
                entity.getPrice(),
                entity.getMaxParticipants(),
                0, // currentParticipants 기본값
                entity.getBank(),
                entity.getAccount(),
                entity.getProgressStatus().toString(),
                entity.getImageUrl(),
                false // isParticipated 기본값
        );
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
                isParticipated
        );
    }

}
