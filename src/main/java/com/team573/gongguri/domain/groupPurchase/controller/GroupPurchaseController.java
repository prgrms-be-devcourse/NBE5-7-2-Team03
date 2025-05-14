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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/group-purchases")
@RequiredArgsConstructor
@Slf4j
public class GroupPurchaseController {
    private final GroupPurchaseService service;
    private final MemberRepository memberRepository;
    private final UnivRepository univRepository;
    private final ChatRoomRepository chatRoomRepository;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GroupPurchaseResponseDto> add(@RequestBody GroupPurchaseRequestDto dto) {
        log.info("[GroupPurchaseController] JSON 방식 게시글 작성 요청 수신");

        Member member = memberRepository.findById(1L)  // 로그인 연동 전 테스트용 고정 ID
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_MEMBER));

        Univ univ = member.getUniv();

        ChatRoom chatRoom = chatRoomRepository.findById(1L)
                .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_CHATROOM));

        return ResponseEntity.ok(service.add(dto, member, chatRoom, univ));
    }
    @GetMapping("/{id}")
    public ResponseEntity<GroupPurchaseResponseDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<List<GroupPurchaseResponseDto>> getAll(Member member) {
        return ResponseEntity.ok(service.getAll(member));
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

    @PostMapping(value = "/upload-test", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadTest(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok("파일 이름: " + file.getOriginalFilename());
    }
}
