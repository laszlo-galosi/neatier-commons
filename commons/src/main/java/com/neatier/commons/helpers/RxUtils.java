/*
 * Copyright (C) 2017 Extremenet Ltd., All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *  All information contained herein is, and remains the property of Extremenet Ltd.
 *  The intellectual and technical concepts contained herein are proprietary to Extremenet Ltd.
 *   and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *   Extremenet Ltd.
 *
 */
package com.neatier.commons.helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action1;
import rx.observables.BlockingObservable;
import rx.observables.SyncOnSubscribe;
import rx.subscriptions.CompositeSubscription;
import trikita.log.Log;

/**
 * Helper class containing io.reactivex related static methods for handling {@link
 * Subscriber}, {@link Observer}'s, {@link Observable}'s.
 *
 * @author László Gálosi
 * @since 13/09/17
 */
public class RxUtils {

    /**
     * static constant flag signing that the application should crash when calling {@link
     * RxUtils#logRxError()} or not.
     */
    private static boolean crashOnRxError;

    /**
     * Unsubscribes from the given subscription if it is not null.
     */
    public static void unsubscribeIfNotNull(Subscription subscription) {
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    /**
     * Creates and returns a new CompositeDisposable with the given resource if the given composite
     * subscription is null or unsubscribed previously. If it is not unsubscribed,it returns the
     * given
     * subscription.
     */
    public static CompositeSubscription getNewCompositeSubIfUnsubscribed(
            @Nullable CompositeSubscription subscription) {
        if (subscription == null || subscription.isUnsubscribed()) {
            return new CompositeSubscription();
        }

        return subscription;
    }

    /**
     * Returns an unmodifiable list from the source {@link Observable<T>}
     *
     * @param source the source Observable which emits the elements to be contained in the list
     * @param capacity the capacity of the returning List
     * @param comparators comparators which sorts the list. All {@link Collections#sort(List)} will
     * be
     * @param <T> the type of the returning elements
     * @return an unmodifiable list from the source Observable
     */
    @SuppressWarnings("unchecked") public static <T> List<T> toUnmutableList(
            final Observable<T> source, int capacity, Comparator<T>... comparators) {
        Iterator<T> iterator = BlockingObservable.from(source).getIterator();
        List<T> list = new ArrayList<>(capacity);
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        for (final Comparator<T> comparator : comparators) {
            Collections.sort(list, comparator);
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Returns a modifiable list from the source {@link Observable<T>}
     *
     * @param source the source Observable which emits the elements to be contained in the list
     * @param capacity the capacity of the returning List
     * @param comparators comparators which sorts the list. All {@link Collections#sort(List)} will
     * be
     * @param <T> the type of the returning elements
     * @return an modifiable list from the source Observable
     */
    @SuppressWarnings("unchecked") public static <T> List<T> toMutableList(Observable<T> source,
            int capacity, Comparator<T>... comparators) {
        List<T> list = new ArrayList<>(capacity);
        Iterator<T> iterator = BlockingObservable.from(source).getIterator();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        for (final Comparator<T> comparator : comparators) {
            Collections.sort(list, comparator);
        }
        return list;
    }

    public static Action1<Throwable> logRxError() {
        final Throwable checkpoint = new Throwable();
        return throwable -> {
            StackTraceElement[] stackTrace = checkpoint.getStackTrace();
            StackTraceElement element = stackTrace[1]; // First element after `crashOnError()`
            String msg = String.format("onError() crash from subscribe() in %s.%s(%s:%s)",
                    element.getClassName(), element.getMethodName(), element.getFileName(),
                    element.getLineNumber());

            //throw new OnErrorNotImplementedException(msg, throwable);
            Log.e("logRxError", new OnErrorNotImplementedException(msg, throwable));
        };
    }

    /**
     * Returns true if the given subscription not nul and not unsubscribed
     */
    public static boolean hasSubscribers(Subscription subscription) {
        return subscription != null && !subscription.isUnsubscribed();
    }

    /**
     * Sets whether the {@link #logRxError()} should crash the application or only print the error
     * which occurred during any point of the Observable stream.
     *
     * @see #logRxError()
     */
    public static void setCrashOnRxError(final boolean creashOnRxError) {
        RxUtils.crashOnRxError = creashOnRxError;
    }

    public static class SubscriberAdapter<T> extends rx.Subscriber<T> {
        @Override public void onCompleted() {
            // no-op by default.
        }

        @Override public void onError(Throwable e) {
        }

        @Override public void onNext(T t) {
            // no-op by default.
        }
    }

    public static <T> T randomFrom(final List<T> items, final Random random) {
        int len = items.size();
        return items.get(random.nextInt(len));
    }

    public static Observable<JsonElement> jsonStreamFromArray(JsonArray jsonArray) {
        if (jsonArray == null) {
            return Observable.empty();
        }
        return Observable.range(0, jsonArray.size())
                .flatMap(i -> Observable.just(jsonArray.get(i)));
    }

    /**
     * Returns an Observable emitting all the property keys contained in the given json object.
     */
    public static Observable<String> jsonKeyStream(JsonObject jsonObject) {
        return Observable.from(JsonSerializer.treeMapFromJson(jsonObject).keySet());
    }

    /**
     * Returns an Observable emitting all the property values contained in the given json object.
     */
    public static Observable<JsonElement> jsonValueStream(JsonObject jsonObject) {
        return Observable.from(JsonSerializer.treeMapFromJson(jsonObject).values());
    }

    /**
     * Returns a comma separated string from the given observable, by calling the {@link
     * Object#toString()} method of emitted objects. Useful for debugging observables.
     */
    @SuppressWarnings("unchecked") public static String asString(Observable observable) {
        StringBuilder sb = new StringBuilder();
        observable.reduce("", (a, b) -> String.format("%s, %s", a.toString(), b.toString()))
                .subscribe(sb::append);
        return sb.toString();
    }

    public static Observable<byte[]> readFile(@NonNull FileInputStream inputStream) {
        final SyncOnSubscribe<FileInputStream, byte[]> fileReader = SyncOnSubscribe.createStateful(
                () -> inputStream, (stream, output) -> {
                    try {
                        final byte[] buffer = new byte[1024];
                        int count = stream.read(buffer);
                        if (count < 0) {
                            output.onCompleted();
                        } else {
                            output.onNext(buffer);
                        }
                    } catch (IOException error) {
                        output.onError(error);
                    }
                    return stream;
                },
                s -> {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        RxUtils.logRxError().call(e);
                    }
                });
        return Observable.create(fileReader);
    }
}
