package com.team573.gongguri.domain.myPage.controller;

import com.team573.gongguri.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/my-page")
public class MyPageViewController {

    @GetMapping("")
    public String showMyPageForm(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        model.addAttribute("nickname", userDetails.getNickname());
        return "/myPage/main";
    }

    @GetMapping("/profile")
    public String showMyProfileForm() {
        return "/myPage/profile";
    }

    @GetMapping("/purchase")
    public String showMyPurchaseForm() {
        return "/myPage/purchase";
    }

}
