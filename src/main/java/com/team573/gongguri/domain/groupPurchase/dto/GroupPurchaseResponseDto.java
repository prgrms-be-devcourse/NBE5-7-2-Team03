package com.team573.gongguri.domain.groupPurchase.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GroupPurchaseResponseDto(
        Long id,
        String title,
        String content,
        int price,
        int maxParticipants,
        int currentParticipants,
        String bank,
        String account,
        String progressStatus,
        String imageUrl,
        Boolean isParticipated,
        String writerEmail,
        String writerNickname,
        Long writerId
) {}