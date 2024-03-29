# Event Emitter

The `EventEmitter` lets you register multiple observers. While there is no observer, the events are enqueued.

To unregister, call `stopListening()` on the `NotificationToken` returned from `eventEmitter.startListening`. You can also use a `CompositeNotificationToken`.

`EventEmitter` implements `EventSource`, so that the `EventEmitter` can be exposed as something to be observed, but cannot be emitted to from the outside.

You can only emit events and listen for events and unregister observers on the thread where you created the `EventEmitter`.

## Example

``` kotlin
// write
private val emitter: EventEmitter<String> = EventEmitter()
val events: EventSource<String> get() = emitter

fun doSomething() {
    emitter.emit("hello")
}

// read
private var subscription: EventSource.NotificationToken? = null

fun observe() {
    subscription = events.startListening { event ->
        showToast(event)
    }
}

fun unsubscribe() {
    subscription?.stopListening()
    subscription = null
}
```

## Example with LifecycleOwner + observe

See https://github.com/Zhuinden/live-event

## Using Event Emitter

In order to use Event Emitter, you need to add jitpack to your project root gradle:

    buildscript {
        repositories {
            // ...
            maven { url "https://jitpack.io" }
        }
        // ...
    }
    allprojects {
        repositories {
            // ...
            maven { url "https://jitpack.io" }
        }
        // ...
    }

In newer projects, you need to also update the `settings.gradle` file's `dependencyResolutionManagement` block:

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }  // <--
        jcenter() // Warning: this repository is going to shut down soon
    }
}
```



and add the dependency to your module level gradle.

    implementation 'com.github.Zhuinden:event-emitter:1.4.0'

## License

    Copyright 2019-2023 Gabor Varadi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
