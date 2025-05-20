package com.team573.gongguri.domain.groupPurchase.dto;

import lombok.Builder;

@Builder
public record GroupPurchaseCreateResponseDto(
        Long id,
        String title,
        String progressStatus,
        String imageUrl
) {}
