package org.darrotech.eventplanner.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("links")
public class LinkController {

    @GetMapping("resume")
    public String aboutMe(){
        return "/user/resume.html";
    }



}
