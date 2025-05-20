package com.team573.gongguri.domain.groupPurchase.dto;

import lombok.Builder;

@Builder
public record GroupPurchaseParticipantResponseDto(
    Long groupParticipantId,
    String nickname,
    Boolean deposit
) {

}
