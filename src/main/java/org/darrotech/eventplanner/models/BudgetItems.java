package org.darrotech.eventplanner.models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
public class BudgetItems extends AbstractEntity{
    @ManyToOne
    Event event;

    @OneToOne//for complete authentication
    private User user;

    @NotNull
    @NotBlank(message = "Item name needed.")
    private String itemName;

    @Valid
    @NotNull
    private BigDecimal itemPrice=new BigDecimal("0.0");

    public BudgetItems() {
    }

    public BudgetItems(Event event, User user, @NotNull String itemName, @NotNull BigDecimal itemPrice) {
        this.event = event;
        this.user = user;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    @Override
    public String toString() {
        return "BudgetItems{" +
                "event=" + event +
                ", user=" + user +
                ", itemName='" + itemName + '\'' +
                ", itemPrice=" + itemPrice +
                '}';
    }
}
