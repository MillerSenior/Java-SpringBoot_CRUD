package org.darrotech.eventplanner.models;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "budget_items")
public class BudgetItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @NotBlank(message = "Item name needed.")
    @Column(nullable = false)
    private String itemName;

    @NotNull
    @NotBlank(message = "Vendor name is required.")
    @Column(nullable = false)
    private String vendor;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be greater than or equal to 0")
    @Digits(integer = 8, fraction = 2, message = "Price must be a valid amount with up to 2 decimal places")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal itemPrice;

    public BudgetItems() {
    }

    public BudgetItems(Event event, User user, @NotNull String itemName, @NotNull String vendor, @NotNull BigDecimal itemPrice) {
        this.event = event;
        this.user = user;
        this.itemName = itemName;
        this.vendor = vendor;
        this.itemPrice = itemPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BudgetItems)) {
            return false;
        }
        BudgetItems that = (BudgetItems) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return String.format("BudgetItems(id=%d, itemName='%s', vendor='%s', itemPrice=%s)", id, itemName, vendor, itemPrice);
    }
}
