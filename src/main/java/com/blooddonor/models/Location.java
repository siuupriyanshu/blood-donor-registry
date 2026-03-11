package com.blooddonor.models;

import java.time.LocalDateTime;

/**
 * Model class representing a location (city, state, country) in the donor registry.
 */
public class Location {
    private int id;
    private String city;
    private String state;
    private String country;
    private LocalDateTime createdAt;

    /**
     * Default constructor.
     */
    public Location() {
    }

    /**
     * Constructor with city, state, and country.
     */
    public Location(String city, String state, String country) {
        this.city = city;
        this.state = state;
        this.country = country;
    }

        /**
         * Constructor with id, city, state, and country.
         */
        public Location(int id, String city, String state, String country) {
            this.id = id;
            this.city = city;
            this.state = state;
            this.country = country;
        }

    /**
     * Constructor with all fields.
     */
    public Location(int id, String city, String state, String country, LocalDateTime createdAt) {
        this.id = id;
        this.city = city;
        this.state = state;
        this.country = country;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return city + ", " + state + ", " + country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return id == location.id &&
               (city != null ? city.equals(location.city) : location.city == null) &&
               (state != null ? state.equals(location.state) : location.state == null) &&
               (country != null ? country.equals(location.country) : location.country == null);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        return result;
    }
}
