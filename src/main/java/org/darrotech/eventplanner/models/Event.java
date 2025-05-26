package org.darrotech.eventplanner.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "events")
public class Event implements Comparable<Event> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<BudgetItems> budgetItemsList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank(message = "Name is required.")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters.")
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @NotNull(message = "Category is required")
    private EventCategory eventCategory;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "event_tags",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private final List<Tag> tags = new ArrayList<>();

    @Valid
    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal budget = new BigDecimal("0.0");

    @Valid
    @NotNull
    @Column(precision = 10, scale = 2)
    private BigDecimal balance = new BigDecimal("0.0");

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "event_details_id")
    @Valid
    @NotNull
    private EventDetails eventDetails;

    public Event() {
    }

    public Event(User user, String name, EventCategory eventCategory, EventDetails eventDetails, BigDecimal budget, BigDecimal balance) {
        this.user = user;
        this.name = name;
        this.eventCategory = eventCategory;
        this.eventDetails = eventDetails;
        this.budget = budget;
        this.balance = balance;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        return getBudget().subtract(getTotalSpent());
    }

    public BigDecimal getTotalSpent() {
        BigDecimal total = new BigDecimal("0.0");
        for (BudgetItems item : getBudgetItemsList()) {
            total = total.add(item.getItemPrice());
        }
        return total;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<BudgetItems> getBudgetItems() {
        return budgetItemsList;
    }

    public List<BudgetItems> getBudgetItemsList() {
        return budgetItemsList;
    }

    public void addBudgetItem(BudgetItems budgetItem) {
        budgetItem.setEvent(this);
        this.budgetItemsList.add(budgetItem);
        this.balance = getBalance();
    }

    public void removeBudgetItem(BudgetItems budgetItem) {
        budgetItemsList.remove(budgetItem);
        budgetItem.setEvent(null);
        this.balance = getBalance();
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        Event event = (Event) o;
        return Objects.equals(getId(), event.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Event obj) {
        return this.eventDetails.getDate().compareTo(obj.getEventDetails().getDate());
    }
}
