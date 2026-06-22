package com.finalrental.models;

import java.time.LocalDate;

/**
 * RentalSearchRequest – immutable value object representing a single
 * rental search query submitted through the website's search form.
 *
 * <p>Use {@link Builder} to construct instances:
 * <pre>
 *   RentalSearchRequest req = new RentalSearchRequest.Builder()
 *       .location("Cairo, Egypt")
 *       .checkIn(LocalDate.now().plusDays(3))
 *       .checkOut(LocalDate.now().plusDays(7))
 *       .carCategory("SUV")
 *       .build();
 * </pre>
 */
public final class RentalSearchRequest {

    private final String    location;
    private final LocalDate checkIn;
    private final LocalDate checkOut;
    private final String    carCategory;
    private final String    pickupTime;
    private final String    dropoffTime;

    // ── Builder ──────────────────────────────────────────────────────────────

    private RentalSearchRequest(Builder b) {
        this.location    = b.location;
        this.checkIn     = b.checkIn;
        this.checkOut    = b.checkOut;
        this.carCategory = b.carCategory;
        this.pickupTime  = b.pickupTime;
        this.dropoffTime = b.dropoffTime;
    }

    public static class Builder {
        private String    location    = "";
        private LocalDate checkIn     = LocalDate.now().plusDays(1);
        private LocalDate checkOut    = LocalDate.now().plusDays(2);
        private String    carCategory = "";
        private String    pickupTime  = "10:00 AM";
        private String    dropoffTime = "10:00 AM";

        public Builder location(String location)       { this.location    = location;    return this; }
        public Builder checkIn(LocalDate checkIn)      { this.checkIn     = checkIn;     return this; }
        public Builder checkOut(LocalDate checkOut)    { this.checkOut    = checkOut;    return this; }
        public Builder carCategory(String category)    { this.carCategory = category;    return this; }
        public Builder pickupTime(String time)         { this.pickupTime  = time;        return this; }
        public Builder dropoffTime(String time)        { this.dropoffTime = time;        return this; }

        public RentalSearchRequest build() {
            if (checkIn == null || checkOut == null) {
                throw new IllegalStateException("checkIn and checkOut are required.");
            }
            if (!checkIn.isBefore(checkOut)) {
                throw new IllegalStateException("checkIn must be before checkOut.");
            }
            return new RentalSearchRequest(this);
        }
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String    getLocation()    { return location; }
    public LocalDate getCheckIn()     { return checkIn; }
    public LocalDate getCheckOut()    { return checkOut; }
    public String    getCarCategory() { return carCategory; }
    public String    getPickupTime()  { return pickupTime; }
    public String    getDropoffTime() { return dropoffTime; }

    public long getRentalDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    @Override
    public String toString() {
        return String.format("RentalSearchRequest{location='%s', checkIn=%s, checkOut=%s, days=%d, category='%s'}",
                location, checkIn, checkOut, getRentalDays(), carCategory);
    }
}
