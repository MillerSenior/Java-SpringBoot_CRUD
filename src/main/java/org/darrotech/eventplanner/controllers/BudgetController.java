package org.darrotech.eventplanner.controllers;

import org.darrotech.eventplanner.data.BudgetItemRepository;
import org.darrotech.eventplanner.data.EventCategoryRepository;
import org.darrotech.eventplanner.data.EventRepository;
import org.darrotech.eventplanner.data.TagRepository;
import org.darrotech.eventplanner.models.BudgetItems;
import org.darrotech.eventplanner.models.Event;
import org.darrotech.eventplanner.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("budgetItems")
public class BudgetController {
    @Autowired
    private BudgetItemRepository budgetItemRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private EventCategoryRepository eventCategoryRepository;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    private AuthenticationController authenticationController;
    //for complete authentication

    @GetMapping("addItem")
    public String displayAddItemForm(@RequestParam Integer eventId, Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);//for complete authentication, also HttpSession
        Optional<Event> optional = eventRepository.findById(eventId);
        Event event = optional.get();
        model.addAttribute("title", "Add Item");
        model.addAttribute("eventId", eventId);
        model.addAttribute(new BudgetItems());
        return "budget/addItem";
    }

    @PostMapping("addItem")
    public String processCreateForm(@RequestParam Integer eventId, @ModelAttribute @Valid BudgetItems budgetItems,
                                    Errors errors, Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);//for complete authentication, also HttpSession
        Optional<Event> optional = eventRepository.findById(eventId);
        Event event = optional.get();
        try {
            if (errors.hasErrors()) {
                model.addAttribute("title", "Add Item");
                model.addAttribute(event);
                model.addAttribute(budgetItems);
                model.addAttribute("errorMsg", "Price entry error: Must be numerical/monetary/decimal value.");
                return "budget/addItem";
                //return "redirect:/budgetItems/addItem?eventId=" + eventId;
            }
        } catch (Exception e) {
            model.addAttribute("title", "Add Item");
            model.addAttribute(event);
            model.addAttribute(budgetItems);
            System.out.println(e.getMessage());
            //this is the handler being caught by the invalid data entry
            model.addAttribute("errorMsg", "Price entry error: Must be numerical/monetary/decimal value.");
            //return "redirect:/budgetItems/addItem?eventId=" + eventId;
            return "budget/addItem";

        }

        budgetItems.setEvent(event);
        budgetItems.setUser(user);
        budgetItemRepository.save(budgetItems);

        return "redirect:/budgetItems/detail?eventId=" + eventId;
    }

    @GetMapping("detail")
    public String displayEventDetails(@RequestParam Integer eventId, Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        Optional<Event> result = eventRepository.findById(eventId);

        if (result.isEmpty()) {
            model.addAttribute("title", "Invalid Event ID: " + eventId);
        } else {
            Event event = result.get();
            model.addAttribute("title", "Budget Details for: " + event.getName());
            model.addAttribute("event", event);
            model.addAttribute("budgetItems", event.getBudgetItemsList());
            model.addAttribute("totalSpent", event.getTotalSpent());
            model.addAttribute("balance", event.getBalance());
        }

        return "budget/budgetList";
    }

    @GetMapping("deleteExpense/{budgetItemsID}")
    public String displayDeleteExpenseForm(Model model, @PathVariable int budgetItemsID) {
        Optional<BudgetItems> b = budgetItemRepository.findById(budgetItemsID);
        BudgetItems budgetItems = b.get();
        model.addAttribute("budgetItems", budgetItems);
        model.addAttribute("title", "Delete Expense");
        return "budget/delete";
    }

    @PostMapping("deleteExpense")
    public String processAssignmentDeleteForm(@RequestParam(required = false) int[] budgetItemsId, @RequestParam Integer eventId) {
        Optional<Event> optional = eventRepository.findById(eventId);
        Event event = optional.get();
        if (budgetItemsId != null) {
            for (int id : budgetItemsId) {
                budgetItemRepository.deleteById(id);

            }
        }
        return "redirect:/budgetItems/detail?eventId=" + eventId;
    }


    @GetMapping("edit/{budgetItemsId}")
    public String editAssignmentInfo(Model model, @PathVariable int budgetItemsId) {
        Optional<BudgetItems> b = budgetItemRepository.findById(budgetItemsId);
        BudgetItems budgetItems = b.get();
        model.addAttribute("budgetItems", budgetItems);
        model.addAttribute("title", "Update expense information for: " + budgetItems.getEvent().getName());

        System.out.println(budgetItems.getId());
        return "budget/edit";
    }

    @PostMapping("edit")
    public String processEditAssignmentInfo(@ModelAttribute @Valid BudgetItems newBudgetItems, Errors errors, Model model, HttpSession session, Event newEvent) {
        User user = authenticationController.getUserFromSession(session);
        int eventId = newBudgetItems.getEvent().getId();
        //Optional<Student> optional = studentRepository.findById(newStudent.getId());
        Optional<Event> optional = eventRepository.findById(eventId);
        Event event = optional.get();
        if (errors.hasErrors()) {
            model.addAttribute("title", "Update event information for: " + event.getName());
            model.addAttribute(event);
            model.addAttribute(newBudgetItems);
            model.addAttribute("errorMsg", "Price entry error: Must be numerical/monetary/decimal value.");
            return "budget/edit";
        }
        newBudgetItems.setUser(user);
        newBudgetItems.setEvent(newBudgetItems.getEvent());
        budgetItemRepository.save(newBudgetItems);

        //return "redirect:/students/detail?studentId=" + newStudent.getId();
        return "redirect:/budgetItems/detail?eventId=" + eventId;
    }
}
