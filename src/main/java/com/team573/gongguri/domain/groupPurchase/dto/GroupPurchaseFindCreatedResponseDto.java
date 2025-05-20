package com.team573.gongguri.domain.groupPurchase.dto;

import lombok.Builder;

@Builder
public record GroupPurchaseFindCreatedResponseDto(
        Long id,
        String title,
        int price,
        int maxParticipants,
        int currentParticipants,
        String progressStatus,
        String imageUrl
) { }
