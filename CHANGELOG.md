# Change log

-Event Emitter 1.3.0 (2022-08-13)
--------------------------------

- Expose `setPaused()` function. Using `setPaused(true)` will prevent events from being emitted, until `setPaused(false)` is called. 

-Event Emitter 1.2.0 (2020-12-24)
--------------------------------

- UPDATE: Make pure JVM library.

- UPDATE: Use command-queue 1.2.0 (pure JVM library).

-Event Emitter 1.1.0 (2020-02-29)
--------------------------------

- CHANGE: Remove dependency on `android.support.annotation.*`. With that, there should be no dependency from the library on either `android.support.*` or `androidx.*`.

Replaced it using `javax.annotation.Nullable` and `javax.annotation.Nonnull` provided by `api("com.google.code.findbugs:jsr305:3.0.2")`.

With these changes, Jetifier should no longer be needed when using Event-Emitter.

-Event Emitter 1.0.0 (2019-09-27)
--------------------------------
- Update command-queue to 1.0.0 (which was just a version bump).

- Revise `event-emitter-sample` to use `LifecycleObserver` and `LifecycleOwner` rather than `CompositeNotificationToken`.


-Event Emitter 0.0.1 (2019-06-04)
--------------------------------
- Initial release.