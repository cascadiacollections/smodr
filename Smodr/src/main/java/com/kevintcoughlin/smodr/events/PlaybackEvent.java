package com.kevintcoughlin.smodr.events;

public class PlaybackEvent {
    public enum Type {
        STOPPED,
        PREPARED,
        PLAYING,
        PAUSED
    }

    public String message;

    public PlaybackEvent(String message) {
        this.message = message;
    }
}