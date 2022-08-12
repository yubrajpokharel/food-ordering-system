package com.food.ordering.system.order.service.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public class StreetAddress {
    private final UUID id;
    private final String street;
    private final String postalCodel;
    private final String city;

    public StreetAddress(UUID id, String street, String postalCodel, String city) {
        this.id = id;
        this.street = street;
        this.postalCodel = postalCodel;
        this.city = city;
    }

    public UUID getId() {
        return id;
    }

    public String getStreet() {
        return street;
    }

    public String getPostalCodel() {
        return postalCodel;
    }

    public String getCity() {
        return city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StreetAddress that = (StreetAddress) o;
        return Objects.equals(id, that.id) && Objects.equals(street, that.street) && Objects.equals(postalCodel, that.postalCodel) && Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, street, postalCodel, city);
    }
}
