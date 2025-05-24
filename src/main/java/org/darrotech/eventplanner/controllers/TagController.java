package org.darrotech.eventplanner.controllers;

import org.darrotech.eventplanner.data.TagRepository;
import org.darrotech.eventplanner.models.Tag;
import org.darrotech.eventplanner.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * Created by Chris Bay
 */
@Controller
@RequestMapping("/tags")
public class TagController {

    @Autowired
    TagRepository tagRepository;
    @Autowired
    private AuthenticationController authenticationController;
    //for complete authentication
    @GetMapping("")
    public String displayTags(Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);//for complete authentication, also HttpSession
        model.addAttribute("title", "All Tags");
        model.addAttribute("tags", tagRepository.findByUserId(user.getId()));
        return "tags/index";
    }

    @GetMapping("/create")
    public String displayCreateTagForm(Model model) {
        model.addAttribute("title", "Create Tag");
        model.addAttribute("tag", new Tag());
        return "tags/create";
    }

    @PostMapping("/create")
    public String processCreateTagForm(@ModelAttribute @Valid Tag tag,
                                       Errors errors, Model model, HttpSession session) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Create Tag");
            model.addAttribute("tag", tag);
            model.addAttribute("errorMsg", "Complete the tag field!");
            return "tags/create";
        }
        User user = authenticationController.getUserFromSession(session);
        tag.setUser(user);
        tagRepository.save(tag);
        return "redirect:/tags";
    }
}
