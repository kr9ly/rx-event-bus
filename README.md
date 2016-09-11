# Rx Event Bus - EventBus based on RxJava

[![Circle CI](https://circleci.com/gh/kr9ly/rx-event-bus/tree/master.svg?style=shield)](https://circleci.com/gh/kr9ly/rx-event-bus/tree/master)

# Dependency

Add this to `repositories` block in your build.gradle

```
jcenter()
```

And Add this to `dependencies` block in your build.gradle

```
compile 'net.kr9ly:rx-event-bus:1.0.0'
```

### Usage

See RxEventBusTest.

```java
RxEventBus parent = new RxEventBus();
// children event propagate to parent
RxEventBus child = new RxEventBus(parent);
// filter event stream by item class (can use your class)
parent.listener(String.class)
        .subscribe(new Action1<RxEvent<String>>() {
            @Override
           public void call(RxEvent<String> event) {
                System.out.println("parent");
            }
        });

child.listener(String.class)
        .subscribe(new Action1<RxEvent<String>>() {
            @Override
            public void call(RxEvent<String> event) {
                System.out.println("child");
            }
        });

child.sender().onNext(testData);
```

# License

```
Copyright 2016 kr9ly

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```