package com.team573.gongguri.domain.groupPurchase.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GroupPurchaseViewController {
    @GetMapping("/test/group-purchase")
    public String testView() {
        return "groupPurchase/group-purchase";
    }
}
