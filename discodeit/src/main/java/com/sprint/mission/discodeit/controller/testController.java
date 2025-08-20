package com.sprint.mission.discodeit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("api/test")
public class testController {

    @RequestMapping("test")
    public String userList(){
        return "redirect:/user-list.html";
    }

}
