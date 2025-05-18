package com.team573.gongguri.domain.groupPurchase.dto;

import java.time.LocalDateTime;

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
        boolean isParticipated
) {}