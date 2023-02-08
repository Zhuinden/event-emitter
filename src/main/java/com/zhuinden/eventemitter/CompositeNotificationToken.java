package com.zhuinden.eventemitter;

import java.util.ArrayList;
import java.util.List;

/**
 * A composite notification allows tracking multiple notification tokens, so that they can be cleared all-at-once.
 */
public class CompositeNotificationToken implements EventSource.NotificationToken {
    private final long threadId = Thread.currentThread().getId();

    private final List<EventSource.NotificationToken> notificationTokens = new ArrayList<>();

    /**
     * Starts tracking the received notification token. Must be called on the same thread where the composite notification token was created.
     *
     * @param notificationToken the notification token
     */
    public void add(EventSource.NotificationToken notificationToken) {
        if (threadId != Thread.currentThread().getId()) {
            throw new IllegalStateException("Cannot add event source on a different thread than where it was created");
        }

        notificationTokens.add(notificationToken);
    }

    private boolean isDisposing = false;

    /**
     * Stops listening, aka stops listening for all tracked notification tokens. Must be called on the same thread where the composite notification token was created.
     */
    @Override
    public void stopListening() {
        if (threadId != Thread.currentThread().getId()) {
            throw new IllegalStateException("Cannot stop listening on a different thread than where it was created");
        }
        if (isDisposing) {
            return;
        }
        isDisposing = true;
        final int size = notificationTokens.size();
        for (int i = size - 1; i >= 0; i--) {
            EventSource.NotificationToken token = notificationTokens.remove(i);
            token.stopListening();
        }
        isDisposing = false;
    }
}
