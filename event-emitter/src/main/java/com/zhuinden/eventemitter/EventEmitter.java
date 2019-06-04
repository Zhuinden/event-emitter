package com.zhuinden.eventemitter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zhuinden.commandqueue.CommandQueue;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventEmitter<E> implements EventSource<E> {
    private final long threadId = Thread.currentThread().getId();

    private CommandQueue<E> commandQueue = new CommandQueue<>();

    private final CommandQueue.Receiver<E> notifyObservers = new CommandQueue.Receiver<E>() {
        @Override
        public void receiveCommand(@NonNull E command) {
            for(int i = observers.size() - 1; i >= 0; i--) {
                observers.get(i).onEventReceived(command);
            }
        }
    };

    private final LinkedList<EventObserver<E>> observers = new LinkedList<>();

    @Override
    public EventSource.NotificationToken startListening(final EventObserver<E> observer) {
        if(threadId != Thread.currentThread().getId()) {
            throw new IllegalStateException("You should register observers only on the thread where the emitter was created");
        }

        observers.add(observer);
        if(observers.size() == 1) {
            commandQueue.setReceiver(notifyObservers);
        }

        return new EventSource.NotificationToken() {
            private boolean isDisposed = false;

            @Override
            public void stopListening() {
                if(threadId != Thread.currentThread().getId()) {
                    throw new IllegalStateException("You should unregister observers only on the thread where the emitter was created");
                }

                if(isDisposed) {
                    throw new IllegalStateException("This observer has already stopped listening!");
                }

                isDisposed = true;

                observers.remove(observer);
                if(observers.size() == 0) {
                    commandQueue.detachReceiver();
                }
            }
        };
    }

    public void emit(@NonNull E event) {
        //noinspection ConstantConditions
        if(event == null) {
            throw new IllegalArgumentException("Event should not be null!");
        }

        if(threadId != Thread.currentThread().getId()) {
            throw new IllegalStateException("You can only emit events on the thread where the emitter was created");
        }

        commandQueue.sendEvent(event);
    }
}