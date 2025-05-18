package com.team573.gongguri.domain.groupPurchase.mapper;

import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchase;
import com.team573.gongguri.domain.groupPurchase.entity.GroupPurchaseParticipant;
import com.team573.gongguri.domain.groupPurchase.entity.ParticipationStatus;
import com.team573.gongguri.domain.member.entity.Member;

public class GroupPurchaseParticipantMapper {

    public static GroupPurchaseParticipant toEntity(GroupPurchase groupPurchase, Member member) {
        return GroupPurchaseParticipant.builder()
                .groupPurchase(groupPurchase)
                .member(member)
                .participationStatus(ParticipationStatus.JOINED)
                .build();
    }
}
