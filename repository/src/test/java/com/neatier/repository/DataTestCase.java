/*
 *  Copyright (C) 2016 Delight Solutions Ltd., All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *
 *  All information contained herein is, and remains the property of Delight Solutions Kft.
 *  The intellectual and technical concepts contained herein are proprietary to Delight Solutions
  *  Kft.
 *   and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *   Delight Solutions Kft.
 */

package com.neatier.repository;

import android.content.Context;
import android.support.annotation.CallSuper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.neatier.commons.helpers.JsonSerializer;
import com.neatier.commons.helpers.LongTaskScheduler;
import com.neatier.data.entity.AutoValueAdapterFactory;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Base class for Robolectric data layer tests.
 * Inherit from this class to create a test.
 *
 * Created by vandekr on 11/02/14.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 21, constants = BuildConfig.class, application = ApplicationStub.class, manifest =
      Config.NONE)
public abstract class DataTestCase {

    public static final String CACHE_FILE_NAME = "Cache.test";

    protected static final int FAKE_CHANNEL_ID_1 = 1;
    protected static final int FAKE_CHANNEL_ID_2 = 2;
    protected static final int FAKE_CHANNEL_ID_3 = 3;
    protected static final String FAKE_CHANNEL_NAME_1 = "Külföld";
    protected static final String FAKE_CHANNEL_NAME_2 = "Belföld";
    protected static final String FAKE_CHANNEL_NAME_3 = "Sport";

    protected static final String JSON_ENTITY_1 = "{"
          + "\"test_id\":" + FAKE_CHANNEL_ID_1 + ","
          + "\"test_name\":\"" + FAKE_CHANNEL_NAME_1 + "\"}";

    protected static final String JSON_ENTITY_2 = "{"
          + "\"test_id\":" + FAKE_CHANNEL_ID_2 + ","
          + "\"test_name\":\"" + FAKE_CHANNEL_NAME_2 + "\"}";

    protected Context mContext;
    protected ShadowApplication mApplication;
    protected TestScheduler mPerformOnScheduler;
    protected TestScheduler mNotifyOnScheduler;
    protected JsonSerializer mJsonSerializer;
    protected Gson mGson;

    @Before
    @CallSuper
    public void setUp() throws Exception {
        //initializeInjectors();
        mApplication = ShadowApplication.getInstance();
        mContext = ShadowApplication.getInstance().getApplicationContext();
        mPerformOnScheduler = new TestScheduler();
        mNotifyOnScheduler = new TestScheduler();
        //JodaTimeAndroid.init(application.getApplicationContext());
        mGson = new GsonBuilder()
              .registerTypeAdapterFactory(new AutoValueAdapterFactory())
              .create();
        mJsonSerializer = new JsonSerializer<>(mGson, new JsonParser());
    }

    /**
     * Helper to test an {@link Observable <T>}'s {@link Observer#onNext(T)} which emits the
     * expected
     * objects of type T.
     *
     * @param testObservable the observable to test.
     * @param subscribeOn the {@link TestScheduler} on which the observable operation performed
     * on.
     * @param observeOn the {@link TestScheduler} on which the observable is observed on.
     * @param expected the expected values of T
     * @param <T> the emitted object type.
     */
    protected <T> void assertObservableHappyCase(final Observable<T> testObservable,
          final TestScheduler subscribeOn, TestScheduler observeOn, final T... expected) {
        List<T> onNextEvents =
              getObservableEvents(testObservable, subscribeOn, observeOn, expected.length);
        assertThat(onNextEvents.size(), is(expected.length));
        assertThat(onNextEvents, is(Arrays.asList(expected)));
    }

    /**
     * Helper to test an {@link Observable <T>}'s {@link Observer#onNext(T)} which emits the
     * expected
     * objects of type T.
     *
     * @param testObservable the observable to test.
     * @param subscribeOn the {@link TestScheduler} on which the observable operation performed
     * on.
     * @param observeOn the {@link TestScheduler} on which the observable is observed on.
     * @param expected the expected values of T
     * @param <T> the emitted object type.
     */
    protected <T> void assertObservableContainsAll(final Observable<T> testObservable,
          final TestScheduler subscribeOn, TestScheduler observeOn, final T... expected) {
        List<T> onNextEvents =
              getObservableEvents(testObservable, observeOn, subscribeOn, expected.length);
        assertThat(onNextEvents.size(), is(expected.length));
        assertThat(onNextEvents.containsAll(Arrays.asList(expected)), is(true));
    }

    /**
     * Helper to test an {@link Observable <List<T>>}'s {@link Observer#onNext(T)} which emits the
     * expected
     * objects of type T.
     *
     * @param testObservable the observable to test.
     * @param subscribeOn the {@link TestScheduler} on which the observable operation performed
     * on.
     * @param observeOn the {@link TestScheduler} on which the observable is observed on.
     * @param expected the expected values of T
     * @param <T> the emitted object type.
     */
    protected <T> void assertListObservableHappyCase(final Observable<List<T>> testObservable,
          final TestScheduler subscribeOn, TestScheduler observeOn, final T... expected) {
        TestSubscriber<List<T>> testSubscriber = new TestSubscriber<>();
        testObservable.subscribe(testSubscriber);

        if (subscribeOn != null && observeOn != null) {
            subscribeOn.triggerActions();
            observeOn.triggerActions();
        }

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertTerminalEvent();
        testSubscriber.assertValueCount(1);

        List<List<T>> onNextEvents = testSubscriber.getOnNextEvents();
        assertThat(onNextEvents.size(), is(1));
        assertThat(onNextEvents.get(0), is(Arrays.asList(expected)));
    }

    protected <T> void assertListObservableEmpty(final Observable<List<T>> testObservable,
          final TestScheduler subscribeOn, TestScheduler observeOn) {
        TestSubscriber<List<T>> testSubscriber = new TestSubscriber<>();
        testObservable.subscribe(testSubscriber);

        if (subscribeOn != null && observeOn != null) {
            subscribeOn.triggerActions();
            observeOn.triggerActions();
        }

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertTerminalEvent();
        testSubscriber.assertValueCount(0);

        List<List<T>> onNextEvents = testSubscriber.getOnNextEvents();
        assertThat(onNextEvents.size(), is(0));
    }

    /**
     * Helper to test an {@link Observable <List<T>>}'s {@link Observer#onNext(T)} which emits the
     * expected
     * objects of type T.
     *
     * @param testObservable the observable to test.
     * @param subscribeOn the {@link TestScheduler} on which the observable operation performed
     * on.
     * @param observeOn the {@link TestScheduler} on which the observable is observed on.
     * @param expected the expected values of T
     * @param <T> the emitted object type.
     */
    protected <T> void assertListObservableContainsAll(final Observable<List<T>> testObservable,
          final TestScheduler subscribeOn, TestScheduler observeOn, final T... expected) {
        List<List<T>> onNextEvents =
              getObservableEvents(testObservable, observeOn, subscribeOn, 1);
        assertThat(onNextEvents.size(), is(1));
        assertThat(onNextEvents.get(0).containsAll(Arrays.asList(expected)), is(true));
    }

    protected <T> List<T> getObservableEvents(final Observable<T> testObservable,
          final TestScheduler subscribeOn, TestScheduler observeOn, final int expectedLength) {
        TestSubscriber<T> testSubscriber = new TestSubscriber<>();
        testObservable.subscribe(testSubscriber);

        if (subscribeOn != null && observeOn != null) {
            subscribeOn.triggerActions();
            observeOn.triggerActions();
        }
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertTerminalEvent();
        testSubscriber.assertValueCount(expectedLength);
        return testSubscriber.getOnNextEvents();
    }

    /**
     * Helper method to test an {@link Observable <T>}'s {@link Observer#onError(Throwable)} fail
     * case.
     *
     * @param testObservable the observable to test.
     * @param subscribeOn the {@link TestScheduler} on which the observable operation performed
     * on.
     * @param observeOn the {@link TestScheduler} on which the observable is observed on.
     * @param expectedErrorClass the class of the expectedError.
     * @param <T> the emitted object type.
     */
    protected <T> void assertObservableSadCase(final Observable<T> testObservable,
          final TestScheduler subscribeOn, TestScheduler observeOn,
          final Class expectedErrorClass) {
        TestSubscriber<T> testSubscriber = new TestSubscriber<>();
        testObservable.subscribe(testSubscriber);
        if (subscribeOn != null && observeOn != null) {
            subscribeOn.triggerActions();
            observeOn.triggerActions();
        }
        testSubscriber.assertTerminalEvent();
        testSubscriber.assertError(expectedErrorClass);
    }

    public void triggerNextAction() {
        mPerformOnScheduler.triggerActions();
        mNotifyOnScheduler.triggerActions();
    }

    public class IsTrue extends ArgumentMatcher<Boolean> {
        public boolean matches(Object o) {
            return (Boolean) o == true;
        }
    }

    public class IsFalse extends ArgumentMatcher<Boolean> {
        public boolean matches(Object o) {
            return (Boolean) o == false;
        }
    }

    public class IsClass extends ArgumentMatcher<Class> {
        final Class clazz;

        public IsClass(final Class clazz) {
            this.clazz = clazz;
        }

        public boolean matches(Object o) {
            return clazz == o;
        }
    }

    protected class TestLongTaksScheduler implements LongTaskScheduler {
        final Scheduler mPerformSheduler;
        final Scheduler mNotifyScheduler;

        public TestLongTaksScheduler(final Scheduler performSheduler,
              final Scheduler notifyScheduler) {
            mPerformSheduler = performSheduler;
            mNotifyScheduler = notifyScheduler;
        }

        @Override public Scheduler performMeOn() {
            return mPerformOnScheduler;
        }

        @Override public Scheduler notifyMeOn() {
            return mNotifyOnScheduler;
        }
    }
}