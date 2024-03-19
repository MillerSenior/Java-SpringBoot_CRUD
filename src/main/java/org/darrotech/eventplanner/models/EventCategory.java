package org.darrotech.eventplanner.models;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris Bay
 */
@Entity
public class EventCategory extends AbstractEntity {
    @OneToOne
    private User user;

    @Valid
    @NotBlank(message = "Category name required!")
    @Size(min = 3, message = "Name must be at least 3 characters long!")
    private String name;

    @OneToMany(mappedBy = "eventCategory")//I can have one category with many events,mapped by
    //has to match value of field in the class being stored in this list
    private final List<Event> events = new ArrayList<>();

    public EventCategory(String name) {
        this.name = name;
    }

    public EventCategory() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Event> getEvents() {
        return events;
    }

    public User getUser() {//for complete authentication
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return name;
    }

}
