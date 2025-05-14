package com.team573.gongguri.domain.chat.entity;

import com.team573.gongguri.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


    @Entity
    @Getter
    @NoArgsConstructor
    @Table(name =
            "chat_room_participation")
    public class ChatRoomParticipation {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long chatRoomParticipantId;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id", nullable = false)
        private Member member;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "chat_room_id", nullable = false)
        private ChatRoom chatRoom;
    }
