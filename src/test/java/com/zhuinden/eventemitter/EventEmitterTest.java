/*
 * Copyright 2018 Gabor Varadi
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

import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EventEmitterTest {
    public abstract static class Events {
        public static class First
            extends Events {
        }

        public static class Second
            extends Events {
        }

        public static class Third
            extends Events {
        }

        public static class Fourth
            extends Events {
        }

        public static class Fifth
            extends Events {
        }

        public static class Sixth
            extends Events {
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && obj.getClass() == getClass();
        }
    }

    @Test
    public void eventEmitterWorks() {
        class Holder<T> {
            T item;

            public Holder(T initial) {
                this.item = initial;
            }
        }

        final Holder<Integer> firsts = new Holder<>(0);
        final Holder<Integer> seconds = new Holder<>(0);

        EventEmitter<Events> emitter = new EventEmitter<>();

        EventEmitter.EventObserver<Events> observer = new EventEmitter.EventObserver<Events>() {
            @Override
            public void onEventReceived(@Nonnull Events command) {
                if(command instanceof Events.First) {
                    firsts.item += 1;
                } else if(command instanceof Events.Second) {
                    seconds.item += 1;
                }
            }
        };

        EventSource.NotificationToken token = emitter.startListening(observer);

        assertThat(firsts.item).isEqualTo(0);
        assertThat(seconds.item).isEqualTo(0);

        emitter.emit(new Events.First());

        assertThat(firsts.item).isEqualTo(1);
        assertThat(seconds.item).isEqualTo(0);

        emitter.emit(new Events.Second());

        assertThat(firsts.item).isEqualTo(1);
        assertThat(seconds.item).isEqualTo(1);

        token.stopListening();

        emitter.emit(new Events.First());
        emitter.emit(new Events.Second());
        emitter.emit(new Events.Second());
        emitter.emit(new Events.Second());

        assertThat(firsts.item).isEqualTo(1);
        assertThat(seconds.item).isEqualTo(1);

        token = emitter.startListening(observer);

        assertThat(firsts.item).isEqualTo(2);
        assertThat(seconds.item).isEqualTo(4);

        token.stopListening();
    }

    @Test
    public void nullEventIsDisallowed() {
        EventEmitter<Void> emitter = new EventEmitter<>();

        try {
            emitter.emit(null);
            Assert.fail();
        } catch(IllegalArgumentException e) {
            // OK!
        }
    }

    @Test
    public void eventEmitterSupportsMultipleListeners() {
        EventEmitter<Events> eventEmitter = new EventEmitter<>();

        eventEmitter.emit(new Events.First());

        final List<Events> firstListener = new LinkedList<>();
        final List<Events> secondListener = new LinkedList<>();
        final List<Events> thirdListener = new LinkedList<>();

        EventSource.NotificationToken first = eventEmitter.startListening(new EventSource.EventObserver<Events>() {
            @Override
            public void onEventReceived(@Nonnull Events event) {
                firstListener.add(event);
            }
        });

        assertThat(firstListener).containsExactly(new Events.First());

        EventSource.NotificationToken second = eventEmitter.startListening(new EventSource.EventObserver<Events>() {
            @Override
            public void onEventReceived(@Nonnull Events event) {
                secondListener.add(event);
            }
        });

        eventEmitter.emit(new Events.Second());

        assertThat(firstListener).containsExactly(new Events.First(), new Events.Second());
        assertThat(secondListener).containsExactly(new Events.Second());

        first.stopListening();

        eventEmitter.emit(new Events.Third());

        assertThat(firstListener).containsExactly(new Events.First(), new Events.Second());
        assertThat(secondListener).containsExactly(new Events.Second(), new Events.Third());

        second.stopListening();

        eventEmitter.emit(new Events.Fourth());

        assertThat(firstListener).containsExactly(new Events.First(), new Events.Second());
        assertThat(secondListener).containsExactly(new Events.Second(), new Events.Third());

        EventSource.NotificationToken third = eventEmitter.startListening(new EventSource.EventObserver<Events>() {
            @Override
            public void onEventReceived(@Nonnull Events event) {
                thirdListener.add(event);
            }
        });

        assertThat(thirdListener).containsExactly(new Events.Fourth());

        third.stopListening();
    }

    @Test
    public void multipleUnregisterAttemptsThrow() {
        EventEmitter<Events> eventEmitter = new EventEmitter<>();

        EventSource.NotificationToken token = eventEmitter.startListening(new EventSource.EventObserver<Events>() {
            @Override
            public void onEventReceived(@Nonnull Events event) {
                // kweh
            }
        });

        token.stopListening();

        try {
            token.stopListening();
            Assert.fail();
        } catch(IllegalStateException e) {
            // OK!
        }
    }

    @Test
    public void setPausedTest() {
        EventEmitter<Events> eventEmitter = new EventEmitter<>();

        eventEmitter.setPaused(true);

        eventEmitter.emit(new Events.First());

        final List<Events> firstListener = new LinkedList<>();
        final List<Events> secondListener = new LinkedList<>();
        final List<Events> thirdListener = new LinkedList<>();

        EventSource.NotificationToken first = eventEmitter.startListening(new EventSource.EventObserver<Events>() {
            @Override
            public void onEventReceived(@Nonnull Events event) {
                firstListener.add(event);
            }
        });

        assertThat(firstListener).containsExactly();

        EventSource.NotificationToken second = eventEmitter.startListening(new EventSource.EventObserver<Events>() {
            @Override
            public void onEventReceived(@Nonnull Events event) {
                secondListener.add(event);
            }
        });

        assertThat(secondListener).containsExactly();

        eventEmitter.emit(new Events.Second());

        assertThat(secondListener).containsExactly();

        eventEmitter.setPaused(false);

        assertThat(firstListener).containsExactly(new Events.First(), new Events.Second());
        assertThat(firstListener).containsExactly(new Events.First(), new Events.Second());

        eventEmitter.setPaused(true);

        eventEmitter.emit(new Events.Third());

        assertThat(firstListener).containsExactly(new Events.First(), new Events.Second());
        assertThat(firstListener).containsExactly(new Events.First(), new Events.Second());
        assertThat(thirdListener).containsExactly();

        EventSource.NotificationToken third = eventEmitter.startListening(new EventSource.EventObserver<Events>() {
            @Override
            public void onEventReceived(@Nonnull Events event) {
                thirdListener.add(event);
            }
        });

        eventEmitter.emit(new Events.Fourth());

        assertThat(firstListener).containsExactly(new Events.First(), new Events.Second());
        assertThat(firstListener).containsExactly(new Events.First(), new Events.Second());
        assertThat(thirdListener).containsExactly();

        eventEmitter.setPaused(false);

        assertThat(firstListener).containsExactly(new Events.First(),
                                                  new Events.Second(),
                                                  new Events.Third(),
                                                  new Events.Fourth());
        assertThat(secondListener).containsExactly(new Events.First(),
                                                   new Events.Second(),
                                                   new Events.Third(),
                                                   new Events.Fourth());
        assertThat(thirdListener).containsExactly(new Events.Third(), new Events.Fourth());

        first.stopListening();
        second.stopListening();
        third.stopListening();
    }

    @Test
    public void eventEmitterComposeNotificationTokenWorks() {
        EventEmitter<Events> eventEmitter = new EventEmitter<>();

        eventEmitter.emit(new Events.First());

        final List<Events> firstListener = new LinkedList<>();
        final List<Events> secondListener = new LinkedList<>();
        final List<Events> thirdListener = new LinkedList<>();

        final List<Events> lastListener = new LinkedList<>();

        final CompositeNotificationToken compositeNotificationToken = new CompositeNotificationToken();

        compositeNotificationToken.add(eventEmitter.startListening(new EventSource.EventObserver<Events>() {
            @Override
            public void onEventReceived(@Nonnull Events event) {
                firstListener.add(event);
            }
        }));

        assertThat(firstListener).containsExactly(new Events.First());

        compositeNotificationToken.add(eventEmitter.startListening(new EventSource.EventObserver<Events>() {
            @Override
            public void onEventReceived(@Nonnull Events event) {
                secondListener.add(event);
            }
        }));

        eventEmitter.emit(new Events.Second());

        assertThat(firstListener).containsExactly(new Events.First(), new Events.Second());
        assertThat(secondListener).containsExactly(new Events.Second());

        eventEmitter.emit(new Events.Third());

        assertThat(firstListener).containsExactly(new Events.First(), new Events.Second(), new Events.Third());
        assertThat(secondListener).containsExactly(new Events.Second(), new Events.Third());

        eventEmitter.emit(new Events.Fourth());

        assertThat(firstListener).containsExactly(new Events.First(),
                                                  new Events.Second(),
                                                  new Events.Third(),
                                                  new Events.Fourth());
        assertThat(secondListener).containsExactly(new Events.Second(), new Events.Third(), new Events.Fourth());

        compositeNotificationToken.add(eventEmitter.startListening(new EventSource.EventObserver<Events>() {
            @Override
            public void onEventReceived(@Nonnull Events event) {
                thirdListener.add(event);
            }
        }));

        assertThat(thirdListener).isEmpty();

        eventEmitter.emit(new Events.Fifth());

        assertThat(firstListener).containsExactly(new Events.First(),
                                                  new Events.Second(),
                                                  new Events.Third(),
                                                  new Events.Fourth(),
                                                  new Events.Fifth());
        assertThat(secondListener).containsExactly(new Events.Second(),
                                                   new Events.Third(),
                                                   new Events.Fourth(),
                                                   new Events.Fifth());
        assertThat(thirdListener).containsExactly(new Events.Fifth());

        compositeNotificationToken.stopListening();

        eventEmitter.emit(new Events.Sixth());

        compositeNotificationToken.add(eventEmitter.startListening(new EventSource.EventObserver<Events>() {
            @Override
            public void onEventReceived(@Nonnull Events event) {
                lastListener.add(event);
            }
        }));

        assertThat(lastListener).contains(new Events.Sixth());
    }
}