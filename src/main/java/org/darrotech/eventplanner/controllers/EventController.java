package org.darrotech.eventplanner.controllers;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.darrotech.eventplanner.data.BudgetItemRepository;
import org.darrotech.eventplanner.data.EventCategoryRepository;
import org.darrotech.eventplanner.data.EventRepository;
import org.darrotech.eventplanner.data.TagRepository;
import org.darrotech.eventplanner.models.Event;
import org.darrotech.eventplanner.models.EventCategory;
import org.darrotech.eventplanner.models.EventDetails;
import org.darrotech.eventplanner.models.Tag;
import org.darrotech.eventplanner.models.User;
import org.darrotech.eventplanner.models.dto.EventTagDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired
    private BudgetItemRepository budgetItemRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventCategoryRepository eventCategoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AuthenticationController authenticationController;

    @GetMapping("")
    public String displayEvents(@RequestParam(required = false) Integer categoryId, Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        if (categoryId == null) {
            model.addAttribute("title", "All Events");
            List<Event> events = eventRepository.findByUserId(user.getId());
            Collections.sort(events);
            model.addAttribute("events", events);
            if (events.isEmpty()) {
                model.addAttribute("pageTitle", "No current Events!");
            } else {
                model.addAttribute("pageTitle", "All of your events: (In order by date)");
            }
        } else {
            Optional<EventCategory> result = eventCategoryRepository.findById(categoryId);
            if (result.isEmpty()) {
                model.addAttribute("pageTitle", "Invalid Category ID: " + categoryId);
            } else {
                EventCategory category = result.get();
                if (category.getEvents().isEmpty()) {
                    model.addAttribute("pageTitle", "No current events in category: " + category.getName());
                } else {
                    model.addAttribute("pageTitle", "Events in category: " + category.getName());
                    model.addAttribute("events", category.getEvents());
                }
            }
        }
        return "events/index1";
    }

    @GetMapping("/create")
    public String renderCreateEventsForm(Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        List<EventCategory> categories = eventCategoryRepository.findByUserId(user.getId());

        if (categories.isEmpty()) {
            model.addAttribute("message", "Please create a category first before creating an event.");
            return "redirect:/eventCategories/create";
        }

        Event event = new Event();
        event.setEventDetails(new EventDetails());
        model.addAttribute("title", "Create Events Form");
        model.addAttribute("event", event);
        model.addAttribute("categories", categories);
        return "events/create";
    }

    @PostMapping("/create")
    public String processCreateEventForm(@ModelAttribute @Valid Event newEvent,
            Errors errors, Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        if (errors.hasErrors()) {
            model.addAttribute("title", "Create Event");
            model.addAttribute("categories", eventCategoryRepository.findByUserId(user.getId()));
            return "events/create";
        }

        // Calculate weekday from date if not already set
        if (newEvent.getEventDetails() != null && newEvent.getEventDetails().getDate() != null) {
            try {
                java.time.LocalDate date = java.time.LocalDate.parse(newEvent.getEventDetails().getDate());
                java.time.DayOfWeek dayOfWeek = date.getDayOfWeek();
                String weekday = dayOfWeek.toString().charAt(0) + dayOfWeek.toString().substring(1).toLowerCase();
                newEvent.getEventDetails().setWeekday(weekday);
            } catch (java.time.format.DateTimeParseException e) {
                // If date parsing fails, leave the default weekday
            }
        }

        newEvent.setUser(user);
        eventRepository.save(newEvent);
        return "redirect:/events";
    }

    @GetMapping("/delete")
    public String renderDeleteEventForm(Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        model.addAttribute("title", "Delete Events");
        model.addAttribute("events", eventRepository.findByUserId(user.getId()));
        return "events/delete";
    }

    @PostMapping("/delete")
    public String processDeleteEventsForm(@RequestParam(required = false) int[] eventIds) {
        if (eventIds != null) {
            for (int id : eventIds) {
                eventRepository.deleteById(id);
            }
        }
        return "redirect:/events";
    }

    @GetMapping("/edit/{eventId}")
    public String displayEditForm(Model model, @PathVariable int eventId, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        Optional<Event> e = eventRepository.findById(eventId);
        if (e.isEmpty()) {
            return "redirect:/events";
        }
        Event event = e.get();
        model.addAttribute("title", "Edit: " + event.getName());
        model.addAttribute("event", event);
        model.addAttribute("categories", eventCategoryRepository.findByUserId(user.getId()));
        model.addAttribute("tags", tagRepository.findByEventsId(event.getId()));
        return "events/edit";
    }

    @PostMapping("/edit")
    public String processEditForm(@ModelAttribute @Valid Event newEvent,
            Errors errors,
            Model model,
            HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        Optional<Event> e = eventRepository.findById(newEvent.getId());
        if (e.isEmpty()) {
            return "redirect:/events";
        }
        Event existingEvent = e.get();
        List<Tag> tags = tagRepository.findByEventsId(existingEvent.getId());
        for (Tag tag : tags) {
            newEvent.addTag(tag);
        }

        // Validate budget
        if (newEvent.getBudget() == null || newEvent.getBudget().compareTo(BigDecimal.ZERO) < 0) {
            errors.rejectValue("budget", "error.budget", "Budget must be a valid positive number");
        }

        if (errors.hasErrors()) {
            model.addAttribute("title", "Edit: " + newEvent.getName());
            model.addAttribute("categories", eventCategoryRepository.findByUserId(user.getId()));
            model.addAttribute("tags", tags);
            return "events/edit";
        }

        // Preserve existing budget items when updating the event
        newEvent.getBudgetItemsList().addAll(existingEvent.getBudgetItemsList());

        // Handle date formatting and weekday calculation
        if (newEvent.getEventDetails() != null && newEvent.getEventDetails().getDate() != null) {
            try {
                java.time.LocalDate date = java.time.LocalDate.parse(newEvent.getEventDetails().getDate());
                java.time.DayOfWeek dayOfWeek = date.getDayOfWeek();
                String weekday = dayOfWeek.toString().charAt(0) + dayOfWeek.toString().substring(1).toLowerCase();
                newEvent.getEventDetails().setWeekday(weekday);
            } catch (java.time.format.DateTimeParseException ex) {
                errors.rejectValue("eventDetails.date", "error.date", "Invalid date format");
                model.addAttribute("title", "Edit: " + newEvent.getName());
                model.addAttribute("categories", eventCategoryRepository.findByUserId(user.getId()));
                model.addAttribute("tags", tags);
                return "events/edit";
            }
        }

        newEvent.setUser(user);
        eventRepository.save(newEvent);
        return "redirect:/events/detail?eventId=" + newEvent.getId();
    }

    @GetMapping("/detail")
    public String displayEventDetails(@RequestParam Integer eventId, Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        Optional<Event> result = eventRepository.findById(eventId);

        if (result.isEmpty()) {
            model.addAttribute("title", "Invalid Event ID: " + eventId);
            model.addAttribute("errorMsg", "Event not found");
            return "redirect:/events";
        } else {
            Event event = result.get();
            model.addAttribute("title", "Details for: " + event.getName());
            model.addAttribute("event", event);
            model.addAttribute("tags", event.getTags());
        }

        return "events/detail";
    }

    @GetMapping("/add-tag")
    public String displayAddTagForm(@RequestParam Integer eventId, Model model, HttpSession session) {
        Optional<Event> result = eventRepository.findById(eventId);
        if (result.isEmpty()) {
            return "redirect:/events";
        }
        Event event = result.get();
        model.addAttribute("title", "Add Tag to: " + event.getName());
        User user = authenticationController.getUserFromSession(session);
        model.addAttribute("tags", tagRepository.findByUserId(user.getId()));
        model.addAttribute("event", event);
        EventTagDTO eventTag = new EventTagDTO();
        eventTag.setEvent(event);
        model.addAttribute("eventTag", eventTag);
        return "events/add-tag";
    }

    @PostMapping("/add-tag")
    public String processAddTagForm(@ModelAttribute @Valid EventTagDTO eventTag,
            Errors errors,
            Model model) {
        if (!errors.hasErrors()) {
            Event event = eventTag.getEvent();
            Tag tag = eventTag.getTag();
            if (!event.getTags().contains(tag)) {
                event.addTag(tag);
                eventRepository.save(event);
            }
            return "redirect:/events/detail?eventId=" + event.getId();
        }
        return "redirect:/events/add-tag?eventId=" + eventTag.getEvent().getId();
    }
}
