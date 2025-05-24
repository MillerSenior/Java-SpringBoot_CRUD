package org.darrotech.eventplanner.models;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "event_details")
public class EventDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Valid
    @Size(max = 500, message = "Description too long.")
    @Column(length = 500)
    private String description;

    @Valid
    @NotBlank(message = "Email Address is required.")
    @Email(message = "Invalid Email try again.")
    @Column(nullable = false)
    private String contactEmail;

    @Valid
    @NotBlank(message = "Location is required.")
    @Column(nullable = false)
    private String location;

    @Valid
    @NotNull(message = "Please specify if event is invitation only.")
    @Column(nullable = false)
    private Boolean invitationOnly = false;

    @Valid
    @NotNull(message = "Please enter number of expected guests.")
    @Min(value = 1, message = "Number of attendees must exceed 1.")
    @Column(nullable = false)
    private Integer numberOfAttendees = 1;

    @Valid
    @NotBlank(message = "Event date is required.")
    @Column(nullable = false)
    private String date;

    @Valid
    @NotNull(message = "Event weekday is required.")
    @Column(nullable = false)
    private String weekday = "Monday";

    @Valid
    @NotNull(message = "Event start time is required.")
    @Column(name = "start_time", nullable = false)
    private String startTime = "12 PM";

    @Valid
    @NotNull(message = "Event end time is required.")
    @Column(name = "end_time", nullable = false)
    private String endTime = "1 PM";

    @Valid
    @OneToOne(mappedBy = "eventDetails")
    private Event event;

    public EventDetails() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getInvitationOnly() {
        return invitationOnly;
    }

    public void setInvitationOnly(Boolean invitationOnly) {
        this.invitationOnly = invitationOnly;
    }

    public Integer getNumberOfAttendees() {
        return numberOfAttendees;
    }

    public void setNumberOfAttendees(Integer numberOfAttendees) {
        this.numberOfAttendees = numberOfAttendees;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFormattedDate() {
        return date + " (" + weekday + ")";
    }
    
    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventDetails)) {
            return false;
        }
        EventDetails that = (EventDetails) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return String.format("EventDetails(id=%d, location='%s')", id, location);
    }
}
