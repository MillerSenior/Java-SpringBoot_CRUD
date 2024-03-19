package org.darrotech.eventplanner.models;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.validation.Valid;
import javax.validation.constraints.*;

@Entity
public class EventDetails extends AbstractEntity {
    @Valid
    @Size(max = 500, message = "Description too long.")
    private String description;

    @Valid
    @NotBlank(message = "Email Address is required.")
    @Email(message = "Invalid Email try again.")
    private String contactEmail;

    @Valid
    @NotBlank(message = "Location is required.")
    private String location;

    @Valid
    @NotNull(message = "Please specify if event is invitation only.")
    private boolean invitationOnly = true;

    @Valid
    @NotNull(message = "Please enter number of expected guests.")
    @Min(value = 1, message = "Number of attendees must exceed 1.")
    private int numberOfAttendees;

    @Valid
    @NotBlank(message = "Event date is required.")
    private String formattedDate = "dd/MM/yyyy";

    @Valid
    @NotNull(message = "Event weekday is required.")
    private String weekday;

    @Valid
    @NotNull(message = "Event start time is required.")
    private String time;

    @Valid
    @NotNull(message = "Event end time is required.")
    private String endTime;

    @Valid
    @OneToOne(mappedBy = "eventDetails")
    private Event event;


    public EventDetails(String description, String contactEmail, String location, boolean invitationOnly, int numberOfAttendees, String formattedDate, String weekday, String time, String endTime) {
        this.description = description;
        this.contactEmail = contactEmail;
        this.location = location;
        this.invitationOnly = invitationOnly;
        this.numberOfAttendees = numberOfAttendees;
        this.formattedDate = formattedDate;
        this.weekday = weekday;
        this.time = time;
        this.endTime=endTime;
    }

    public EventDetails() {
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isInvitationOnly() {
        return invitationOnly;
    }

    public void setInvitationOnly(boolean invitationOnly) {
        this.invitationOnly = invitationOnly;
    }

    public int getNumberOfAttendees() {
        return numberOfAttendees;
    }

    public void setNumberOfAttendees(int numberOfAttendees) {
        this.numberOfAttendees = numberOfAttendees;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }



}

