package com.team573.gongguri.domain.groupPurchase.dto;

import lombok.Builder;

@Builder
public record GroupPurchaseRequestDto(
        String title,
        String content,
        int price,
        int maxParticipants,
        String bank,
        String account,
        String progressStatus,
        String imageUrl
) {}
