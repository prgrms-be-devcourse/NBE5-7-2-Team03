package com.team573.gongguri.domain.chat.service;


import com.team573.gongguri.domain.chat.dto.ChatMessageRequestDto;
import com.team573.gongguri.domain.chat.dto.ChatMessageResponseDto;
import com.team573.gongguri.domain.chat.entity.ChatMessage;
import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.mapper.ChatMessageMapper;
import com.team573.gongguri.domain.chat.repository.ChatMessageRepository;
import com.team573.gongguri.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageResponseDto addChatMessage(
        Long roomId,
        ChatMessageRequestDto requestDto
    ) {
        ChatMessage createdMessage = chatMessageRepository.save(
            ChatMessageMapper.toEntity(roomId, requestDto.nickname(), requestDto.content())
        );
        return ChatMessageMapper.toChatMessageResponseDto(createdMessage);
    }

    public Long addChatRoom(Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.save(new ChatRoom());
        return chatRoom.getChatRoomId();
    }
}
