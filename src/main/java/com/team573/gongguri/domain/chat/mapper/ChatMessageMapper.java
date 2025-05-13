package com.team573.gongguri.domain.chat.mapper;

import com.team573.gongguri.domain.chat.dto.ChatMessageResponseDto;
import com.team573.gongguri.domain.chat.entity.ChatMessage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageMapper {
    public static ChatMessage toEntity(Long roomId, String nickname, String content) {
        return ChatMessage.builder()
            .roomId(roomId)
            .nickname(nickname)
            .content(content)
            .build();
    }

    public static ChatMessageResponseDto toChatMessageResponseDto(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
            .content(chatMessage.getContent())
            .nickname(chatMessage.getNickname())
            .build();
    }
}
