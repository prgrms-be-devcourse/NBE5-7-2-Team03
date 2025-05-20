package com.team573.gongguri.domain.review.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReviewViewController {
    @GetMapping("/test")
    public String review() {
        return "review/review";
    }
}
