package com.team573.gongguri.domain.groupPurchase.dto;

public record GroupPurchaseRequestDto(
        String title,
        String content,
        int price,
        int maxParticipants,
        String bank,
        String account,
        String progressStatus
) {}
