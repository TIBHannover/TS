package uk.ac.ebi.spot.ols.controller.dto;

import java.time.LocalDateTime;

public class RestCallRequest {
    private String address;
    private LocalDateTime dateTimeFrom;
    private LocalDateTime dateTimeTo;

    public RestCallRequest() {
    }

    public RestCallRequest(String address, LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo) {
        this.address = address;
        this.dateTimeFrom = dateTimeFrom;
        this.dateTimeTo = dateTimeTo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getDateTimeFrom() {
        return dateTimeFrom;
    }

    public void setDateTimeFrom(LocalDateTime dateTimeFrom) {
        this.dateTimeFrom = dateTimeFrom;
    }

    public LocalDateTime getDateTimeTo() {
        return dateTimeTo;
    }

    public void setDateTimeTo(LocalDateTime dateTimeTo) {
        this.dateTimeTo = dateTimeTo;
    }
}
