/*
 * Copyright 2019 Gabor Varadi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhuinden.eventemitter;

import javax.annotation.Nonnull;

import com.zhuinden.commandqueue.CommandQueue;

import java.util.LinkedList;

/**
 * The event emitter lets you register multiple observers, but enqueues events while there are no observers.
 *
 * @param <E> the event type
 */
public class EventEmitter<E> implements EventSource<E> {
    private final long threadId = Thread.currentThread().getId();

    private CommandQueue<E> commandQueue = new CommandQueue<>();

    private final CommandQueue.Receiver<E> notifyObservers = new CommandQueue.Receiver<E>() {
        @Override
        public void receiveCommand(@Nonnull E command) {
            for(int i = observers.size() - 1; i >= 0; i--) {
                observers.get(i).onEventReceived(command);
            }
        }
    };

    private final LinkedList<EventObserver<E>> observers = new LinkedList<>();

    /**
     * @inheritDoc
     */
    @Nonnull
    @Override
    public final EventSource.NotificationToken startListening(@Nonnull final EventObserver<E> observer) {
        if(threadId != Thread.currentThread().getId()) {
            throw new IllegalStateException("You should register observers only on the thread where the emitter was created");
        }

        //noinspection ConstantConditions
        if(observer == null) {
            throw new IllegalArgumentException("Observer should not be null!");
        }

        observers.add(observer);
        if(observers.size() == 1) {
            commandQueue.setReceiver(notifyObservers);
        }

        return new EventSource.NotificationToken() {
            private boolean isDisposed = false;

            @Override
            public final void stopListening() {
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

    /**
     * Emits events to all observers. If there are no observers, it is enqueued until there is at least one observer.
     *
     * @param event the event
     */
    public final void emit(@Nonnull E event) {
        //noinspection ConstantConditions
        if(event == null) {
            throw new IllegalArgumentException("Event should not be null!");
        }

        if(threadId != Thread.currentThread().getId()) {
            throw new IllegalStateException("You can only emit events on the thread where the emitter was created");
        }

        commandQueue.sendEvent(event);
    }

    /**
     * When paused, the event emitter will not emit any events until it is no longer paused
     *
     * @param paused if event emitter should be paused
     */
    public final void setPaused(boolean paused) {
        commandQueue.setPaused(paused);
    }
}