package com.team573.gongguri.domain.grouppurchase.dto;

import lombok.Builder;

@Builder
public record GroupPurchaseDetailResponseDto(
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
) { }
