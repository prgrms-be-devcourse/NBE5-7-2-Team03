package com.team573.gongguri.domain.chat.dto;

import lombok.Builder;

@Builder
public record ChatMessageResponseDto(
    String nickname,
    String content
) {

}
