package com.team573.gongguri.domain.groupPurchase.dto;

import lombok.Builder;

@Builder
public record GroupPurchaseListResponseDto(
        Long id,
        String title,
        int price,
        String progressStatus,
        int currentParticipants,
        int maxParticipants,
        String imageUrl
) { }
