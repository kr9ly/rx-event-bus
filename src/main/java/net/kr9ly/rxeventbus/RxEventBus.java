package net.kr9ly.rxeventbus;

import rx.Observable;
import rx.Observer;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Copyright 2016 kr9ly
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class RxEventBus {

    private final Subject<Object, Object> eventQueue;

    private final ConnectableObservable<RxEvent<?>> rootObservable;

    public RxEventBus() {
        this(null);
    }

    public RxEventBus(final RxEventBus parent) {
        eventQueue = new SerializedSubject<Object, Object>(PublishSubject.create());

        rootObservable = Observable.concat(
                eventQueue.map(new Func1<Object, RxEvent<?>>() {
                    @Override
                    public RxEvent<?> call(Object o) {
                        return new RxEvent<Object>(o);
                    }
                }).map(new Func1<RxEvent<?>, Observable<? extends RxEvent<?>>>() {
                    @Override
                    public Observable<? extends RxEvent<?>> call(final RxEvent<?> event) {
                        return Observable
                                .just(event)
                                .doOnCompleted(new Action0() {
                                    @Override
                                    public void call() {
                                        if (parent != null && event.isPropagating()) {
                                            parent.eventQueue.onNext(event.getItem());
                                        }
                                    }
                                });
                    }
                })
        ).publish();

        rootObservable.connect();
    }

    @SuppressWarnings("unchecked")
    public <T> Observer<T> sender() {
        return (Observer<T>) eventQueue;
    }

    public <T> Observable<RxEvent<T>> listener(final Class<T> clazz) {
        return rootObservable.onBackpressureBuffer().filter(new Func1<RxEvent<?>, Boolean>() {
            @Override
            public Boolean call(RxEvent<?> event) {
                return clazz.isInstance(event.getItem());
            }
        }).map(new Func1<RxEvent<?>, RxEvent<T>>() {
            @Override
            @SuppressWarnings("unchecked")
            public RxEvent<T> call(RxEvent<?> event) {
                return (RxEvent<T>) event;
            }
        });
    }

}
