package com.team573.gongguri.domain.groupPurchase.controller;

import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseParticipantResponseDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseRequestDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseResponseDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseWithChatResponseDto;
import com.team573.gongguri.domain.groupPurchase.entity.ProgressStatus;
import com.team573.gongguri.domain.groupPurchase.service.GroupPurchaseParticipantService;
import com.team573.gongguri.domain.groupPurchase.service.GroupPurchaseService;
import com.team573.gongguri.global.security.CustomUserDetails;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/group-purchases")
@RequiredArgsConstructor
@Slf4j
public class GroupPurchaseController {
    private final GroupPurchaseService groupPurchaseService;
    private final GroupPurchaseParticipantService groupPurchaseParticipantService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupPurchaseResponseDto> add(
            @RequestBody GroupPurchaseRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        log.info("[GroupPurchaseController] JSON 방식 게시글 작성 요청 수신");
        String email = customUserDetails.getUsername();
        GroupPurchaseResponseDto createdDto = groupPurchaseService.add(dto, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupPurchaseResponseDto> get(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(groupPurchaseService.get(id, email));
    }

    @Deprecated
    @GetMapping
    public ResponseEntity<List<GroupPurchaseResponseDto>> getAll(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(groupPurchaseService.getAll(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupPurchaseResponseDto> update(
            @PathVariable Long id,
            @RequestBody GroupPurchaseRequestDto dto
    ) {
        return ResponseEntity.ok(groupPurchaseService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        groupPurchaseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Void> join(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        String email = customUserDetails.getUsername();
        groupPurchaseService.join(id, email);
        log.info("member joined: {}", email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/chat")
    public ResponseEntity<List<GroupPurchaseWithChatResponseDto>> getWithChat(
            @RequestParam(required = false, name = "cursor") Long cursorGroupPurchaseId,
            @RequestParam(required = false) String progressStatus,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        List<ProgressStatus> progressStatuses = new ArrayList<>();

        if (progressStatus != null) {
            if (progressStatus.equals("RECRUITING")) {
                progressStatuses.add(ProgressStatus.RECRUITING);
                progressStatuses.add(ProgressStatus.CLOSED);
            } else if (progressStatus.equals("COMPLETED")) {
                progressStatuses.add(ProgressStatus.COMPLETED);
            }
        }

        List<GroupPurchaseWithChatResponseDto> withMessages
                = groupPurchaseService.getWithMessage(size, cursorGroupPurchaseId, progressStatuses, customUserDetails.getMemberId());
        return ResponseEntity.ok(withMessages);
    }

    @PatchMapping("/{groupPurchaseId}/participants/{participantsId}/confirm")
    public ResponseEntity<Void> confirmDeposit(
        @PathVariable Long groupPurchaseId,
        @PathVariable Long participantsId,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        groupPurchaseParticipantService.confirmDeposit(groupPurchaseId, participantsId, customUserDetails.getMemberId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{groupPurchaseId}/participants/{participantsId}/cancel")
    public ResponseEntity<Void> cancelParticipantStatus(
        @PathVariable Long groupPurchaseId,
        @PathVariable Long participantsId,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        groupPurchaseParticipantService.cancelParticipation(groupPurchaseId, participantsId, customUserDetails.getMemberId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupPurchaseId}/participants")
    public ResponseEntity<List<GroupPurchaseParticipantResponseDto>> getParticipants(
        @PathVariable Long groupPurchaseId,
        @RequestParam(required = false, name = "cursor") Long cursorParticipantId,
        @RequestParam(required = false) Boolean deposit,
        @RequestParam(defaultValue = "10") int size,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        log.info("{}", deposit);
        List<GroupPurchaseParticipantResponseDto> participants = groupPurchaseParticipantService.getParticipants(
            groupPurchaseId, cursorParticipantId, deposit, customUserDetails.getMemberId(), size);

        return ResponseEntity.ok(participants);
    }

    @GetMapping("/cursor")
    public ResponseEntity<List<GroupPurchaseResponseDto>> getAllByCursor(
            @RequestParam(required = false, name = "cursor") Long cursorGroupPurchaseId,
            @RequestParam(required = false) String progressStatus,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        List<ProgressStatus> progressStatuses = new ArrayList<>();

        if (progressStatus != null) {
            if (progressStatus.equals("RECRUITING")) {
                progressStatuses.add(ProgressStatus.RECRUITING);
                progressStatuses.add(ProgressStatus.CLOSED);
            } else if (progressStatus.equals("COMPLETED")) {
                progressStatuses.add(ProgressStatus.COMPLETED);
            }
        }

        List<GroupPurchaseResponseDto> groupPurchases
                = groupPurchaseService.getAllByCursor(
                cursorGroupPurchaseId,
                progressStatuses,
                size,
                customUserDetails.getUsername()
        );

        return ResponseEntity.ok(groupPurchases);
    }

}
