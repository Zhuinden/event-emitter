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

/**
 * An event source represents something that can be observed.
 *
 * A {@link NotificationToken} can be used to unregister.
 *
 * @param <E> the event type
 */
public interface EventSource<E> {
    /**
     * The event observer can receive events emitted by the {@link EventSource}.
     *
     * @param <E> the event type
     */
    interface EventObserver<E> {
        /**
         * Called when an event is received.
         *
         * @param event the event
         */
        void onEventReceived(@Nonnull E event);
    }

    /**
     * The notification token can be used to unregister.
     */
    interface NotificationToken {
        /**
         * Stops listening to the event source.
         */
        void stopListening();
    }

    /**
     * Register to and start listening to the event source for events.
     *
     * @param eventObserver the observer
     * @return the notification token, used to unregister the observer.
     */
    @Nonnull
    NotificationToken startListening(@Nonnull EventObserver<E> eventObserver);
}
