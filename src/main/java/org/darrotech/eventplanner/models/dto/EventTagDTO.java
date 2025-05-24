package org.darrotech.eventplanner.models.dto;

import org.darrotech.eventplanner.models.Event;
import org.darrotech.eventplanner.models.Tag;

import jakarta.validation.constraints.NotNull;

//wraps together the event and tag classes, allows us to tie together in a bundle
public class EventTagDTO {

    @NotNull
    private Event event;

    @NotNull
    private Tag tag;

    //empty constructor
    public EventTagDTO() {
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
