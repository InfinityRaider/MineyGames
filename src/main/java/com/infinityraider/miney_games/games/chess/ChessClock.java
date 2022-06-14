package com.infinityraider.miney_games.games.chess;

import com.google.common.collect.Sets;

import java.util.Set;

public class ChessClock {
    private static final int MILLIS_IN_SEC = 1000;
    private static final int SECS_IN_MIN = 60;
    private static final int MINS_IN_HOUR = 60;

    // actual counter value
    private long milliCounter;

    // system millis on last call
    private long lastMillis;

    // flag for running
    boolean running;

    // trackers for displaying the time
    private int hours;
    private int minutes;
    private int seconds;

    // callbacks
    private final Set<ICallback> callbacks;

    public ChessClock() {
        this(10 * SECS_IN_MIN * MILLIS_IN_SEC);
    }

    public ChessClock(long millis) {
        this.milliCounter = millis;
        this.updateTime();
        this.callbacks = Sets.newIdentityHashSet();
    }

    public boolean isRunning() {
        return this.running;
    }

    public void start() {
        if(this.isTimeUp()) {
            return;
        }
        this.running = true;
        this.tick();
    }

    public void tick() {
        if(this.isTimeUp() || !this.isRunning()) {
            return;
        }
        long newMillis = System.currentTimeMillis();
        this.milliCounter = Math.max(0, this.milliCounter - (newMillis - this.lastMillis));
        this.updateTime();
        this.lastMillis = newMillis;
        if(this.milliCounter <= 0) {
            this.running = false;
            this.callbacks.forEach(callback -> callback.onTimeUp(this));
        }
    }

    public void stop() {
        if(this.isTimeUp()) {
            return;
        }
        this.tick();
        this.running = false;
        this.callbacks.forEach(callback -> callback.onMove(this));
    }

    public boolean isTimeUp() {
        return this.getTime() <= 0;
    }

    public long getTime() {
        return this.milliCounter;
    }

    public void addTime(long millis) {
        this.milliCounter += millis;
        this.updateTime();
    }

    public int getHours() {
        return this.hours;
    }

    public int getMinutes() {
        return this.minutes;
    }

    public int getSeconds() {
        return this.seconds;
    }

    protected void updateTime() {
        long millis = this.getTime();
        this.hours = (int) (millis / (MINS_IN_HOUR * SECS_IN_MIN * MILLIS_IN_SEC));
        this.minutes = (int) ( (millis - (this.getHours() * MINS_IN_HOUR * SECS_IN_MIN * MILLIS_IN_SEC)) / (SECS_IN_MIN * MINS_IN_HOUR) );
        this.seconds = (int) ( (millis - (this.getHours() + MINS_IN_HOUR + this.getMinutes()) * SECS_IN_MIN * MILLIS_IN_SEC) / (MILLIS_IN_SEC) );
    }

    public ChessClock addCallback(ICallback callback) {
        this.callbacks.add(callback);
        return this;
    }

    public interface ICallback {
        default void onMove(ChessClock clock) {}

        default void onTimeUp(ChessClock clock) {}
    }

}
