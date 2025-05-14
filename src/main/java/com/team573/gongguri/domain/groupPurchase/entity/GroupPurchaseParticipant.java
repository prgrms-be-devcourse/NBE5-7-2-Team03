package com.team573.gongguri.domain.groupPurchase.entity;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "group_purchase_participant")
    public class GroupPurchaseParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupParticipantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupPurchase groupPurchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Enum<ParticipationStatus> participationStatus;
}
