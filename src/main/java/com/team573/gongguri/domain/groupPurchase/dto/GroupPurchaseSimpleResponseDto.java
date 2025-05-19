package com.team573.gongguri.domain.groupPurchase.dto;

import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import lombok.Builder;

@Builder
public record GroupPurchaseSimpleResponseDto(
    Long id,
    String title,
    int maxParticipants,
    Long participantCount,
    ProgressStatus progressStatus,
    String imageUrl,
    int price
) {
}
