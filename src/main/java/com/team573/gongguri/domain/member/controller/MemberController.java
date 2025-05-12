package com.team573.gongguri.domain.member.controller;

import com.team573.gongguri.domain.member.dto.JoinRequestDto;
import com.team573.gongguri.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 폼 표시 API
    @GetMapping("/login")
    public String showLoginForm() {
        return "/member/login";
    }

    @GetMapping("/join")
    public String showJoinForm() {return "/member/join";}

    @GetMapping("/")
    public String showIndexForm() {return "/member/index";}


    @PostMapping("/join")
    public String join(@ModelAttribute JoinRequestDto joinRequest, RedirectAttributes redirectAttributes) {

        memberService.join(joinRequest);
        redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다!");
        return "redirect:/login";
    }

}
