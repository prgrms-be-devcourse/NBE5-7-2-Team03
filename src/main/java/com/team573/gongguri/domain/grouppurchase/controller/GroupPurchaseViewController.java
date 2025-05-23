package com.team573.gongguri.domain.grouppurchase.controller;


import ch.qos.logback.core.spi.ErrorCodes;
import com.team573.gongguri.domain.member.entity.Member;
import com.team573.gongguri.domain.member.repository.MemberRepository;
import com.team573.gongguri.global.exception.CustomErrorCode;
import com.team573.gongguri.global.exception.CustomException;
import com.team573.gongguri.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
@RequestMapping("/group-purchase")
@RequiredArgsConstructor
public class GroupPurchaseViewController {

    private final MemberRepository memberRepository;

    // 공동구매 목록 페이지
    @GetMapping("")
    public String showHomePage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Member member = memberRepository.findById(userDetails.getMemberId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_MEMBER));

        model.addAttribute("univName", member.getUniv().getUnivName());
        return "groupPurchase/group-purchase";
    }
    // 공동구매 게시글 작성 페이지
    @GetMapping("/post")
    public String showCreatePage() {
        return "groupPurchase/group-purchase-post";
    }

    // 공동구매 게시글 상세 페이지
    @GetMapping("/{id}")
    public String showDetailPage(@PathVariable Long id, Model model) {
        model.addAttribute("groupId", id);
        return "groupPurchase/group-purchase-read";
    }

    // 공동구매 게시글 수정 페이지
    @GetMapping("/update")
    public String showUpdatePage() {
        return "groupPurchase/group-purchase-update";
    }

    @GetMapping("/chats")
    public String showChats() {
        return "groupPurchase/group-purchase-chats";
    }

    @GetMapping("/{groupPurchaseId}/participants")
    public String manageParticipants(@PathVariable Long groupPurchaseId, Model model) {
        model.addAttribute("groupPurchaseId", groupPurchaseId);
        return "groupPurchase/participants-manage";
    }
}
