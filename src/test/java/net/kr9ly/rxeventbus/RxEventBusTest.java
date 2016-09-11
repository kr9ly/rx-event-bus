package net.kr9ly.rxeventbus;

import org.junit.Test;
import rx.functions.Action1;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class RxEventBusTest {

    @Test
    public void testSimplePropagation() {
        final String testData = "TEST_DATA";
        final Map<String, String> results = new HashMap<String, String>();

        RxEventBus bus = new RxEventBus();
        bus.listener(String.class)
                .subscribe(new Action1<RxEvent<String>>() {
                    @Override
                    public void call(RxEvent<String> event) {
                        results.put("bus", event.getItem());
                    }
                });

        bus.sender().onNext(testData);

        assertEquals(testData, results.get("bus"));
    }

    @Test
    public void testOtherClassSubscription() {
        final String testData = "TEST_DATA";
        final Integer testData2 = 12345;

        final Map<String, Object> results = new HashMap<String, Object>();

        RxEventBus bus = new RxEventBus();
        bus.listener(String.class)
                .subscribe(new Action1<RxEvent<String>>() {
                    @Override
                    public void call(RxEvent<String> event) {
                        results.put("string", event.getItem());
                    }
                });

        bus.listener(Integer.class)
                .subscribe(new Action1<RxEvent<Integer>>() {
                    @Override
                    public void call(RxEvent<Integer> event) {
                        results.put("integer", event.getItem());
                    }
                });

        bus.sender().onNext(testData);
        bus.sender().onNext(testData2);

        assertEquals(testData, results.get("string"));
        assertEquals(testData2, results.get("integer"));
    }

    @Test
    public void testMultiPropagation() {
        final String testData = "TEST_DATA";
        final Map<String, String> results = new HashMap<String, String>();

        RxEventBus bus = new RxEventBus();
        bus.listener(String.class)
                .subscribe(new Action1<RxEvent<String>>() {
                    @Override
                    public void call(RxEvent<String> event) {
                        results.put("bus1", event.getItem());
                    }
                });

        bus.listener(String.class)
                .subscribe(new Action1<RxEvent<String>>() {
                    @Override
                    public void call(RxEvent<String> event) {
                        results.put("bus2", event.getItem());
                    }
                });

        bus.sender().onNext(testData);

        assertEquals(testData, results.get("bus1"));
        assertEquals(testData, results.get("bus2"));
    }

    @Test
    public void testParentPropagation() {
        final String testData = "TEST_DATA";
        final Map<String, String> results = new HashMap<String, String>();

        RxEventBus parent = new RxEventBus();
        RxEventBus child = new RxEventBus(parent);
        parent.listener(String.class)
                .subscribe(new Action1<RxEvent<String>>() {
                    @Override
                    public void call(RxEvent<String> event) {
                        results.put("parent", event.getItem());
                    }
                });

        child.sender().onNext(testData);

        assertEquals(testData, results.get("parent"));
    }

    @Test
    public void testChildAndParentPropagation() {
        final String testData = "TEST_DATA";
        final Map<String, String> results = new HashMap<String, String>();

        RxEventBus parent = new RxEventBus();
        RxEventBus child = new RxEventBus(parent);
        parent.listener(String.class)
                .subscribe(new Action1<RxEvent<String>>() {
                    @Override
                    public void call(RxEvent<String> event) {
                        results.put("parent", event.getItem());
                    }
                });

        child.listener(String.class)
                .subscribe(new Action1<RxEvent<String>>() {
                    @Override
                    public void call(RxEvent<String> event) {
                        results.put("child", event.getItem());
                    }
                });

        child.sender().onNext(testData);

        assertEquals(testData, results.get("child"));
        assertEquals(testData, results.get("parent"));
    }

    @Test
    public void testStopPropagation() {
        final String testData = "TEST_DATA";
        final Map<String, String> results = new HashMap<String, String>();

        RxEventBus parent = new RxEventBus();
        RxEventBus child = new RxEventBus(parent);
        parent.listener(String.class)
                .subscribe(new Action1<RxEvent<String>>() {
                    @Override
                    public void call(RxEvent<String> event) {
                        results.put("parent", event.getItem());
                    }
                });

        child.listener(String.class)
                .subscribe(new Action1<RxEvent<String>>() {
                    @Override
                    public void call(RxEvent<String> event) {
                        results.put("child1", event.getItem());
                        event.stopPropagation();
                    }
                });

        child.listener(String.class)
                .subscribe(new Action1<RxEvent<String>>() {
                    @Override
                    public void call(RxEvent<String> event) {
                        results.put("child2", event.getItem());
                    }
                });

        child.sender().onNext(testData);

        assertEquals(testData, results.get("child1"));
        assertEquals(testData, results.get("child2"));
        assertNull(results.get("parent"));
    }
}
