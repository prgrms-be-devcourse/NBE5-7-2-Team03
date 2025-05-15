package com.team573.gongguri.domain.groupPurchase.controller;

import com.team573.gongguri.domain.chat.repository.ChatRoomRepository;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseRequestDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseResponseDto;
import com.team573.gongguri.domain.groupPurchase.service.GroupPurchaseService;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.domain.member.repository.UnivRepository;
import com.team573.gongguri.global.exception.ErrorCode;
import com.team573.gongguri.global.exception.ErrorException;
import com.team573.gongguri.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group-purchases")
@RequiredArgsConstructor
@Slf4j
public class GroupPurchaseController {
    private final GroupPurchaseService service;
    private final MemberRepository memberRepository;


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupPurchaseResponseDto> add(
            @RequestBody GroupPurchaseRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        log.info("[GroupPurchaseController] JSON 방식 게시글 작성 요청 수신");
        String email = customUserDetails.getUsername();
        GroupPurchaseResponseDto createdDto = service.add(dto, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<GroupPurchaseResponseDto> get(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));
        return ResponseEntity.ok(service.get(id, member));
    }

    @GetMapping
    public ResponseEntity<List<GroupPurchaseResponseDto>> getAll(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = memberRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));

        return ResponseEntity.ok(service.getAll(member));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupPurchaseResponseDto> update(
            @PathVariable Long id,
            @RequestBody GroupPurchaseRequestDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{id}/join")
    public ResponseEntity<Void> join(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        Member member = memberRepository.findByEmail(customUserDetails.getUsername())
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));
        service.join(id, member);
        log.info("member joined: {}", member.getEmail());
        return ResponseEntity.noContent().build();
    }
}
