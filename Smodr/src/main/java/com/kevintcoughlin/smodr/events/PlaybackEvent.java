package com.kevintcoughlin.smodr.events;

public final class PlaybackEvent {
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