package com.example.javafx_project.service;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.function.Consumer;

public final class TimeService {
    private Timeline timeline;
    private int total, left;
    private final Consumer<Integer> onTick;
    private final Runnable onFinish;

    public TimeService(int totalSeconds, Consumer<Integer> onTick, Runnable onFinish) {
        this.total = Math.max(0, totalSeconds);
        this.left = this.total;
        this.onTick = onTick;
        this.onFinish = onFinish;
    }

    public void start() {
        if (total == 0) return;
        stop(); // ensure not double-started

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            left = Math.max(0, left - 1);
            onTick.accept(left);
            if (left <= 0) {
                stop();
                onFinish.run();
            }
        }));
        timeline.setCycleCount(total);
        timeline.playFromStart();
    }

    public void stop() {
        if (timeline != null) { timeline.stop(); timeline = null; }
    }

    public int getLeft()  { return left; }
    public int getTotal() { return total; }
}
