package com.is4103.backend.model;

public class EventViews {

    // Bare minumum event data
    public static class Basic {
    };

    // Event data for public view
    public static class Public extends Basic {
    };

    public static class Private extends Public {
    };
}