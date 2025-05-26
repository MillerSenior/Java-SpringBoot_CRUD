package org.darrotech.eventplanner.controllers;

import java.util.Optional;

import org.darrotech.eventplanner.data.BudgetItemRepository;
import org.darrotech.eventplanner.data.EventRepository;
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
@RequestMapping("/budgetItems")
public class BudgetItemsController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BudgetItemRepository budgetItemRepository;

    @Autowired
    private AuthenticationController authenticationController;

    @GetMapping
    public String displayBudgetItems(@RequestParam Integer eventId, Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        Optional<Event> result = eventRepository.findById(eventId);

        if (result.isEmpty()) {
            return "redirect:/events";
        }

        Event event = result.get();
        if (!event.getUser().getId().equals(user.getId())) {
            return "redirect:/events";
        }

        model.addAttribute("title", "Budget Items - " + event.getName());
        model.addAttribute("event", event);
        return "budgetItems/index";
    }

    @GetMapping("/create")
    public String displayCreateBudgetItemForm(@RequestParam Integer eventId, Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        Optional<Event> result = eventRepository.findById(eventId);

        if (result.isEmpty()) {
            return "redirect:/events";
        }

        Event event = result.get();
        if (!event.getUser().getId().equals(user.getId())) {
            return "redirect:/events";
        }

        model.addAttribute("title", "Add Expense - " + event.getName());
        model.addAttribute("event", event);
        model.addAttribute("budgetItems", new BudgetItems());
        return "budgetItems/create";
    }

    @PostMapping("/create")
    public String processCreateBudgetItemForm(@ModelAttribute @Valid BudgetItems budgetItems,
            Errors errors,
            @RequestParam Integer eventId,
            Model model,
            HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        Optional<Event> result = eventRepository.findById(eventId);

        if (result.isEmpty()) {
            return "redirect:/events";
        }

        Event event = result.get();
        if (!event.getUser().getId().equals(user.getId())) {
            return "redirect:/events";
        }

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Expense - " + event.getName());
            model.addAttribute("event", event);
            return "budgetItems/create";
        }

        // Set up the relationships
        budgetItems.setUser(user);
        event.addBudgetItem(budgetItems); // This will also set the event on budgetItems

        // Save the event which will cascade to the budget item
        eventRepository.save(event);

        return "redirect:/budgetItems?eventId=" + eventId;
    }

    @GetMapping("/edit")
    public String displayEditForm(@RequestParam Integer id, Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        Optional<BudgetItems> result = budgetItemRepository.findById(id);

        if (result.isEmpty()) {
            return "redirect:/events";
        }

        BudgetItems budgetItem = result.get();
        Event event = budgetItem.getEvent();

        if (!event.getUser().getId().equals(user.getId())) {
            return "redirect:/events";
        }

        model.addAttribute("title", "Edit Expense");
        model.addAttribute("budgetItem", budgetItem);
        model.addAttribute("event", event);
        return "budgetItems/edit";
    }

    @PostMapping("/edit")
    public String processEditForm(@ModelAttribute @Valid BudgetItems budgetItem,
            Errors errors,
            Model model,
            HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        Optional<BudgetItems> existingItem = budgetItemRepository.findById(budgetItem.getId());

        if (existingItem.isEmpty()) {
            return "redirect:/events";
        }

        BudgetItems item = existingItem.get();
        Event event = item.getEvent();

        if (!event.getUser().getId().equals(user.getId())) {
            return "redirect:/events";
        }

        if (errors.hasErrors()) {
            model.addAttribute("title", "Edit Expense");
            model.addAttribute("event", event);
            model.addAttribute("errorMsg", "Budget entry error: Must be numerical/monetary/decimal value.");
            return "budgetItems/edit";
        }

        // Preserve relationships
        budgetItem.setEvent(event);
        budgetItem.setUser(user);

        budgetItemRepository.save(budgetItem);
        return "redirect:/budgetItems?eventId=" + event.getId();
    }

    @PostMapping("/delete")
    public String deleteItem(@RequestParam Integer id, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        Optional<BudgetItems> result = budgetItemRepository.findById(id);

        if (result.isEmpty()) {
            return "redirect:/events";
        }

        BudgetItems budgetItem = result.get();
        Event event = budgetItem.getEvent();

        if (!event.getUser().getId().equals(user.getId())) {
            return "redirect:/events";
        }

        budgetItemRepository.deleteById(id);
        return "redirect:/budgetItems?eventId=" + event.getId();
    }
}
