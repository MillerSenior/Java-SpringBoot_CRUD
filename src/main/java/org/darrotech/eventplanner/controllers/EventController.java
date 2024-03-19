package org.darrotech.eventplanner.controllers;


import org.darrotech.eventplanner.data.BudgetItemRepository;
import org.darrotech.eventplanner.data.EventCategoryRepository;
import org.darrotech.eventplanner.data.EventRepository;
import org.darrotech.eventplanner.data.TagRepository;
import org.darrotech.eventplanner.models.Event;
import org.darrotech.eventplanner.models.EventCategory;
import org.darrotech.eventplanner.models.Tag;
import org.darrotech.eventplanner.models.User;
import org.darrotech.eventplanner.models.dto.EventTagDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("events")
public class EventController {
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


    @GetMapping
    public String displayEvents(@RequestParam(required = false) Integer categoryId, Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);//for complete authentication, also HttpSession
        if (categoryId == null) {
            model.addAttribute("title", "All Events");
            //get events that belong to single user
            List<Event> events = eventRepository.findByUserId(user.getId());
            Collections.sort(events);//should sort by date, using compareTo method in Event class
            //add list to page
            model.addAttribute("events", events);
            if (events.size() == 0) {
                model.addAttribute("pageTitle", "No current Events!");
            } else {
                model.addAttribute("pageTitle", "All of your events: (In order by date)");
            }
            System.out.println(events.size());
        } else {
            //find event categories that have been passed into this page by id
            Optional<EventCategory> result = eventCategoryRepository.findById(categoryId);
            if (result.isEmpty()) {
                model.addAttribute("pageTitle", "Invalid Category ID: " + categoryId);
            } else {
                EventCategory category = result.get();
                if (category.getEvents().size() == 0) {
                    model.addAttribute("pageTitle", "No current events in category: " + category.getName());
                } else {
                    model.addAttribute("pageTitle", "Events in category: " + category.getName());
                    //if this page is accessed by category id, events will be accessed by this attribute
                    model.addAttribute("events", category.getEvents());
                }

            }
        }
        return "events/index1";
    }


    //lives at /events/create
    @GetMapping("create")
    public String renderCreateEventsForm(Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        model.addAttribute("title", "Create Events Form");
        model.addAttribute(new Event());//used to display information about the Event fields,
        // this info will be used in template to help render the form more efficiently
        // model.addAttribute("weekday", DayOfWeek.values());//returns array of values from DayOfWeek
        model.addAttribute("categories", eventCategoryRepository.findByUserId(user.getId()));
        return "events/create";
    }

    @PostMapping("create")
    public String processCreateEventForm(@ModelAttribute @Valid Event newEvent,
                                         Errors errors, Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        if (errors.hasErrors()) {
            model.addAttribute("title", "Create Event");
            model.addAttribute("categories", eventCategoryRepository.findByUserId(user.getId()));
            model.addAttribute("errorMsg", "Budget entry error: Must be numerical/monetary/decimal value.");
            return "events/create";
        }
        newEvent.setUser(user);
        eventRepository.save(newEvent);
        return "redirect:";
    }


    @GetMapping("delete")
    public String renderDeleteEventForm(Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        model.addAttribute("title", "Delete Events");
        model.addAttribute("events", eventRepository.findByUserId(user.getId()));
        return "events/delete";
    }

    @PostMapping("delete")
    public String processDeleteEventsForm(@RequestParam(required = false) int[] eventIds) {
        if (eventIds != null) {
            for (int id : eventIds) {
                eventRepository.deleteById(id);
            }
        }
        return "redirect:";
    }

    @GetMapping("edit/{eventId}")
    public String displayEditForm(Model model, @PathVariable int eventId, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        // controller code will go here
        Optional<Event> e = eventRepository.findById(eventId);
        Event event = e.get();
        model.addAttribute("title", "Edit: " + event.getName());
        model.addAttribute("event", event);
        model.addAttribute("categories", eventCategoryRepository.findByUserId(user.getId()));
        model.addAttribute("tags", tagRepository.findByEventsId(event.getId()));
        List<Tag> tags = tagRepository.findByEventsId(event.getId());
        for (Tag tag : tags) {
            System.out.println("Tag from the display method: " + tag.getDisplayName());
        }
        System.out.println("Event id from the display method: " + event.getId());
        return "events/edit";
    }

    @PostMapping("edit")
    public String processEditForm(@ModelAttribute @Valid Event newEvent, Errors errors, Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        Optional<Event> e = eventRepository.findById(newEvent.getId());
        Event event = e.get();
        List<Tag> tags = tagRepository.findByEventsId(event.getId());
        for (Tag tag : tags) {
            newEvent.addTag(tag);
            System.out.println("Tag from the process method: " + tag.getDisplayName());
        }
        if (errors.hasErrors()) {
            model.addAttribute("categories", eventCategoryRepository.findByUserId(user.getId()));
            model.addAttribute("tags", tags);
            model.addAttribute("errorMsg", "Budget entry error: Must be numerical/monetary/decimal value.");
            return "events/edit";
        }
        System.out.println("Event id from the process method: " + event.getId());
        newEvent.setUser(user);
        //newEvent.addTag((tags);
        eventRepository.save(newEvent);
        return "redirect:detail?eventId=" + newEvent.getId();
    }

    @GetMapping("detail")
    public String displayEventDetails(@RequestParam Integer eventId, Model model, HttpSession session) {
        User user = authenticationController.getUserFromSession(session);
        Optional<Event> result = eventRepository.findById(eventId);

        if (result.isEmpty()) {
            model.addAttribute("title", "Invalid Event ID: " + eventId);
        } else {
            Event event = result.get();
            model.addAttribute("title", " Details for: " + event.getName());
            model.addAttribute("event", event);
            model.addAttribute("tags", event.getTags());
        }

        return "events/detail";
    }

    @GetMapping("add-tag")
    public String displayAddTagForm(@RequestParam Integer eventId, Model model, HttpSession session) {
        Optional<Event> result = eventRepository.findById(eventId);//create optional object
        Event event = result.get();//get event object from the optional object
        //pass desired information into view
        model.addAttribute("title", "Add Tag to: " + event.getName());
        User user = authenticationController.getUserFromSession(session);
        model.addAttribute("tags", tagRepository.findByUserId(user.getId()));//list all of the tags
        model.addAttribute("event", event);//so form knows which event is having a tag added to it
        EventTagDTO eventTag = new EventTagDTO();//use model binding to pass in a single object with shared information
        eventTag.setEvent(event);
        model.addAttribute("eventTag", eventTag);
        return "events/add-tag.html";
    }

    @PostMapping("add-tag")
    public String processAddTagForm(@ModelAttribute @Valid EventTagDTO eventTag,
                                    Errors errors,
                                    Model model) {

        if (!errors.hasErrors()) {//get the event and tag from the dto, and then add the tag to the tags list
            Event event = eventTag.getEvent();
            Tag tag = eventTag.getTag();
            if (!event.getTags().contains(tag)) {//if the tags list for the event doesn't already contain the tag...
                event.addTag(tag);
                eventRepository.save(event);//now that the event has been modified save again...
            }
            return "redirect:detail?eventId=" + event.getId();
        }

        return "redirect:add-tag";
    }


}
