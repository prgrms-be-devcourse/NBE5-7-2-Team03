package com.team573.gongguri.domain.groupPurchase.controller;

import com.team573.gongguri.domain.chat.entity.ChatRoom;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseRequestDto;
import com.team573.gongguri.domain.groupPurchase.dto.GroupPurchaseResponseDto;
import com.team573.gongguri.domain.groupPurchase.service.GroupPurchaseService;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.entity.Univ;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/group-purchase")
@RequiredArgsConstructor
public class GroupPurchaseController {
    private final GroupPurchaseService service;

    @PostMapping
    public ResponseEntity<GroupPurchaseResponseDto> add(@RequestBody GroupPurchaseRequestDto dto) {
        Member dummyMember = new Member();
        Univ dummyUniv = new Univ();
        ChatRoom dummyChatRoom = new ChatRoom();
        return ResponseEntity.ok(service.add(dto, dummyMember, dummyChatRoom, dummyUniv));

    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupPurchaseResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<List<GroupPurchaseResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

}
