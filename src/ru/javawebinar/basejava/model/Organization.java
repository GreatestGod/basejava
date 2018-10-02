package ru.javawebinar.basejava.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * gkislin
 * 19.07.2016
 */
public class Organization {
    private final Link homePage;
    private final List<Description> descriptions = new ArrayList<>();


    public Organization(String name, String url, LocalDate startDate, LocalDate endDate, String title, String description) {
        Objects.requireNonNull(startDate, "startDate must not be null");
        Objects.requireNonNull(endDate, "endDate must not be null");
        Objects.requireNonNull(title, "title must not be null");
        this.homePage = new Link(name, url);
        descriptions.add(new Description(startDate, endDate, title, description));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return Objects.equals(homePage, that.homePage) &&
                Objects.equals(descriptions, that.descriptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(homePage, descriptions);
    }

    @Override
    public String toString() {
        return "Organization{" +
                "homePage=" + homePage +
                ", descriptions=" + descriptions +
                '}';
    }

    private static class Description {
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final String title;
        private final String description;

        public Description(LocalDate startDate, LocalDate endDate, String title, String description) {
            this.startDate = startDate;
            this.endDate = endDate;
            this.title = title;
            this.description = description;
        }
    }
}
