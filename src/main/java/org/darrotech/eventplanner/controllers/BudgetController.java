package org.darrotech.eventplanner.controllers;

import java.util.Optional;

import org.darrotech.eventplanner.data.BudgetItemRepository;
import org.darrotech.eventplanner.data.EventRepository;
import org.darrotech.eventplanner.data.UserRepository;
import org.darrotech.eventplanner.models.BudgetItems;
import org.darrotech.eventplanner.models.Event;
import org.darrotech.eventplanner.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("budget")
public class BudgetController {

    @Autowired
    private BudgetItemRepository budgetItemRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationController authenticationController;

    @GetMapping("add")
    public String displayAddItemForm(@RequestParam Integer eventId, Model model, HttpSession session) {
        Optional<Event> result = eventRepository.findById(eventId);
        Event event = result.get();
        model.addAttribute("title", "Add Budget Item to: " + event.getName());
        model.addAttribute("event", event);
        model.addAttribute(new BudgetItems());
        return "budget/add";
    }

    @PostMapping("add")
    public String processCreateForm(@RequestParam Integer eventId, @ModelAttribute @Valid BudgetItems budgetItems,
            Errors errors, Model model, HttpSession session) {
        Optional<Event> result = eventRepository.findById(eventId);
        Event event = result.get();
        User user = authenticationController.getUserFromSession(session);

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Budget Item to: " + event.getName());
            model.addAttribute("event", event);
            return "budget/add";
        }

        budgetItems.setEvent(event);
        budgetItems.setUser(user);
        budgetItemRepository.save(budgetItems);
        return "redirect:/events/detail?eventId=" + event.getId();
    }

    @GetMapping("detail")
    public String displayEventDetails(@RequestParam Integer eventId, Model model, HttpSession session) {
        Optional<Event> result = eventRepository.findById(eventId);
        Event event = result.get();
        model.addAttribute("title", "Budget Details for: " + event.getName());
        model.addAttribute("event", event);
        model.addAttribute("budgetItems", event.getBudgetItems());
        return "budget/detail";
    }

    @GetMapping("delete")
    public String displayDeleteForm(@RequestParam Integer eventId, Model model) {
        Optional<Event> result = eventRepository.findById(eventId);
        Event event = result.get();
        model.addAttribute("title", "Delete Budget Items for: " + event.getName());
        model.addAttribute("event", event);
        model.addAttribute("budgetItems", event.getBudgetItems());
        return "budget/delete";
    }

    @PostMapping("delete")
    public String processDeleteForm(@RequestParam(required = false) int[] budgetIds) {
        if (budgetIds != null) {
            for (int id : budgetIds) {
                budgetItemRepository.deleteById(id);
            }
        }
        return "redirect:";
    }

    @GetMapping("edit/{budgetId}")
    public String displayEditForm(Model model, @RequestParam Integer budgetId) {
        Optional<BudgetItems> budgetItem = budgetItemRepository.findById(budgetId);
        BudgetItems budgetItems = budgetItem.get();
        model.addAttribute("title", "Edit Budget Item: " + budgetItems.getItemName());
        model.addAttribute("budgetItems", budgetItems);
        return "budget/edit";
    }

    @PostMapping("edit")
    public String processEditAssignmentInfo(@ModelAttribute @Valid BudgetItems newBudgetItems, Errors errors, Model model, HttpSession session, Event newEvent) {
        if (errors.hasErrors()) {
            model.addAttribute("title", "Edit Budget Item: " + newBudgetItems.getItemName());
            return "budget/edit";
        }
        budgetItemRepository.save(newBudgetItems);
        return "redirect:/events/detail?eventId=" + newBudgetItems.getEvent().getId();
    }
}
