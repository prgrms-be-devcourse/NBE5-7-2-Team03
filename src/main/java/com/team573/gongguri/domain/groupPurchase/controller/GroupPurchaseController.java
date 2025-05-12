package com.team573.gongguri.domain.groupPurchase.controller;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.chat.repository.ChatRoomRepository;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseRequestDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseResponseDto;
import com.team573.gongguri.domain.groupPurchase.service.GroupPurchaseService;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.domain.member.repository.UnivRepository;
import com.team573.gongguri.global.exception.ErrorCode;
import com.team573.gongguri.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group-purchases")
@RequiredArgsConstructor
public class GroupPurchaseController {
    private final GroupPurchaseService service;
    private final MemberRepository memberRepository;
    private final UnivRepository univRepository;
    private final ChatRoomRepository chatRoomRepository;

    @PostMapping
    public ResponseEntity<GroupPurchaseResponseDto> add(@RequestBody GroupPurchaseRequestDto dto) {
        Member member = memberRepository.findById(1L)  // 로그인 연동 전 테스트용 고정 ID
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));

        Univ univ = univRepository.findById(dto.univId())
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_UNIV));

        ChatRoom chatRoom = chatRoomRepository.findById(dto.chatRoomId())
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_UNIV));

        return ResponseEntity.ok(service.add(dto, member, chatRoom, univ));

    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupPurchaseResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<List<GroupPurchaseResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupPurchaseResponseDto> update(@PathVariable Long id,
                                                           @RequestBody GroupPurchaseRequestDto dto) {

        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
