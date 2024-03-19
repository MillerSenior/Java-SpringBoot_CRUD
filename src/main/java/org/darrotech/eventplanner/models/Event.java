package org.darrotech.eventplanner.models;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Event extends AbstractEntity implements Comparable<Event> {

    @OneToMany(mappedBy = "event")
    private final List<BudgetItems> budgetItemsList = new ArrayList<>();

    @ManyToOne//for authentication/new/getter and setter
    private User user;

    @NotBlank(message = "Name is required.")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters.")
    private String name;

    @ManyToOne//I can have many events in one category
    @NotNull(message = "Category is required")
    private EventCategory eventCategory;

    @ManyToMany//many events can have many tags
    private final List<Tag> tags = new ArrayList<>();


    @OneToOne(cascade = CascadeType.ALL)//one set of details to one event
    @Valid
    @NotNull
    private EventDetails eventDetails;

    @Valid
    @NotNull
    private BigDecimal budget = new BigDecimal("0.0");

    @Valid
    @NotNull
    private BigDecimal balance = new BigDecimal("0.0");

    public Event(User user, String name, EventCategory eventCategory, EventDetails eventDetails, BigDecimal budget, BigDecimal balance) {
        this.user = user;
        this.name = name;
        this.eventCategory = eventCategory;
        this.eventDetails = eventDetails;
        this.budget = budget;
        this.balance = balance;
    }

    /*
     A no-arg constructor has been created. It simply sets the id of the object, leaving all other fields null.
            The previously-existing constructor now calls this(), which calls the no-arg constructor to set the id before setting the values of all other fields.
    
             */
    public Event() {
    }

    public BigDecimal getBalance() {
        this.balance=balance.add(getBudget().subtract(getTotalSpent()));
        return balance;
    }
    public BigDecimal getTotalSpent() {
        BigDecimal total = new BigDecimal("0.0");
        //total=total.add(getBudget().subtract(getBalance()));
        for (int i=0; i<getBudgetItemsList().size(); i++) {
            total=total.add(getBudgetItemsList().get(i).getItemPrice());
        }
        return total;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<BudgetItems> getBudgetItemsList() {
        return budgetItemsList;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventCategory getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(EventCategory eventCategory) {
        this.eventCategory = eventCategory;
    }

    public EventDetails getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(EventDetails eventDetails) {
        this.eventDetails = eventDetails;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public int compareTo(Event obj) {//for alphabetization
        return eventDetails.getFormattedDate().compareToIgnoreCase(obj.eventDetails.getFormattedDate());
    }


}

