package com.adanali.java.model;

import java.time.LocalDateTime;

public class Alarm implements Comparable<Alarm>{

    private LocalDateTime dateTime;
    private String title;

    public Alarm(LocalDateTime dateTime, String title) {
        this.dateTime = dateTime;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    @Override
    public int compareTo(Alarm other) {
        return this.dateTime.compareTo(other.getDateTime());
    }
}
