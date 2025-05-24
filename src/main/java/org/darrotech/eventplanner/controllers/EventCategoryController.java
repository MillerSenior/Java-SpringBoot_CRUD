package org.darrotech.eventplanner.controllers;

import org.darrotech.eventplanner.data.EventCategoryRepository;
import org.darrotech.eventplanner.models.EventCategory;
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
@RequestMapping("/eventCategories")
public class EventCategoryController {
    @Autowired
    private AuthenticationController authenticationController;
    //for complete authentication
    @Autowired
    private EventCategoryRepository eventCategoryRepository;

    @GetMapping("")
    public String displayAllCategories(Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);//for complete authentication, also HttpSession
        model.addAttribute("title", "All Categories");
        model.addAttribute("categories", eventCategoryRepository.findByUserId(user.getId()));
        return "eventCategories/index";
    }

    @GetMapping("/create")
    public String renderCreateEventCategoryForm(Model model) {
        model.addAttribute("title", "Create Category");
        model.addAttribute("eventCategory", new EventCategory());
        return "eventCategories/create";
    }

    @PostMapping("/create")
    public String processCreateEventCategoryForm(@Valid @ModelAttribute EventCategory eventCategory,
                                                 Errors errors, Model model, HttpSession session) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Create Category");
            model.addAttribute("eventCategory", eventCategory);
            model.addAttribute("errorMsg", "Category name required!");
            return "eventCategories/create";
        }
        User user = authenticationController.getUserFromSession(session);
        eventCategory.setUser(user);
        eventCategoryRepository.save(eventCategory);
        return "redirect:/eventCategories";
    }

}
