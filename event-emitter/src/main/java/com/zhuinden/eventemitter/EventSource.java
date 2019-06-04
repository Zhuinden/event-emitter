package com.zhuinden.eventemitter;

public interface EventSource<E> {
    interface EventObserver<E> {
        void onEventReceived(E event);
    }

    interface NotificationToken {
        void stopListening();
    }

    NotificationToken startListening(EventObserver<E> eventObserver);
}
