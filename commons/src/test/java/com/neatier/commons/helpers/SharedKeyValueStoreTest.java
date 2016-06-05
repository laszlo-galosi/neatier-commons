/*
 *  Copyright (C) 2016 Delight Solutions Ltd., All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *
 *  All information contained herein is, and remains the property of Delight Solutions Kft.
 *  The intellectual and technical concepts contained herein are proprietary to Delight Solutions Kft.
 *   and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *   Delight Solutions Kft.
 */

package com.neatier.commons.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.neatier.commons.CommonsTestCase;
import com.neatier.commons.exception.InternalErrorException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.robolectric.shadows.ShadowApplication;

import rx.Observable;
import rx.schedulers.TestScheduler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by László Gálosi on 04/08/15
 */
public class SharedKeyValueStoreTest extends CommonsTestCase {

    private static final String PREF_FILE_NAME = "entities";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private SharedPreferences mSharedPreferences;
    private SharedKeyValueStore<String, Object> mSharedKeyValueStore;
    private TestScheduler mPerformMeOnScheduler;
    private TestScheduler mNotifyMeOnScheduler;

    @Before
    public void setUp() {
        ShadowApplication application = ShadowApplication.getInstance();
        Context context = application.getApplicationContext();
        mSharedPreferences = application.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        mSharedKeyValueStore = new SharedKeyValueStore<>(context, PREF_FILE_NAME);
        mPerformMeOnScheduler = new TestScheduler();
        mNotifyMeOnScheduler = new TestScheduler();
    }

    @After
    public void tearDown() throws Exception {
        mSharedKeyValueStore.clear().commit();
    }

    @Test
    public void testKeysAsStream() throws Exception {
        mSharedKeyValueStore.put("key1", "value1").put("key2", "value2").commit();
        assertObservableContainsAll(mSharedKeyValueStore.keysAsStream(), null, null, "key1",
                "key2");
    }

    @Test
    public void testKeysAsListStream() throws Exception {
        mSharedKeyValueStore.put("key1", "value1").put("key2", "value2").commit();
        assertListObservableContainsAll(mSharedKeyValueStore.keysAsListStream(), null, null, "key1",
                "key2");
    }

    @Test
    public void testValuesAsStream_WithDifferentKinds() throws Exception {
        Long longValue = 0L;
        Integer intValue = 0;
        Float floatValue = 0.0f;
        Boolean booleanValue = false;
        String stringValue = "value";

        mSharedKeyValueStore.put("long", longValue)
                .put("int", intValue)
                .put("float", floatValue)
                .put("boolean", booleanValue)
                .put("string", stringValue)
                .commit();
        assertObservableContainsAll(mSharedKeyValueStore.valuesAsStream(), null, null,
                longValue, intValue, floatValue, booleanValue, stringValue);
    }

    @Test
    public void testValuesAsListStream_WithDifferentKind() throws Exception {
        Long longValue = 0L;
        Integer intValue = 0;
        Float floatValue = 0.0f;
        Boolean booleanValue = false;
        String stringValue = "value";

        mSharedKeyValueStore.put("long", longValue)
                .put("int", intValue)
                .put("float", floatValue)
                .put("boolean", booleanValue)
                .put("string", stringValue)
                .commit();
        assertListObservableContainsAll(mSharedKeyValueStore.valuesAsListStream(), null, null,
                longValue, intValue, floatValue, booleanValue,
                stringValue);
    }

    @Test
    public void test_WithoutCommit_ShouldReturnEmptyKeysAsStream() {
        mSharedKeyValueStore.put("key1", "value1").put("key2", "value2");
        assertObservableHappyCase(mSharedKeyValueStore.keysAsStream(), null, null);
    }

    @Test
    public void test_GetOrEmptyWithoutPut_ShouldReturnEmptyObservable() {
        assertObservableHappyCase(mSharedKeyValueStore.getOrEmpty("key1"), null, null);
    }

    @Test
    public void test_GetOrErrorWithPut_ShouldReturnValueObservable() {
        mSharedKeyValueStore.put("key1", "value1").put("key2", "value2").commit();
        assertObservableHappyCase(mSharedKeyValueStore.getOrError("key1",
                new InternalErrorException(
                        "Not found")), null, null,
                "value1");
    }

    @Test
    public void test_GetOrErrorWithoutPut_ShouldReturnErrorObservable() {
        assertObservableSadCase(
                mSharedKeyValueStore.getOrError("key1", new InternalErrorException("no key found")),
                null, null, InternalErrorException.class);
    }

    @Test
    public void test_GetOrDefaultWithoutPut_ShouldReturnDefaultObservable() {
        assertObservableHappyCase(mSharedKeyValueStore.getOrJustDefault("key1", "default_key"),
                null, null, "default_key");
    }

    @Test
    public void test_PutAsyncHappyCase() {
        assertObservableHappyCase(mSharedKeyValueStore.putAsync("key1", "value1"),
                mPerformMeOnScheduler, mNotifyMeOnScheduler, true);
        assertObservableHappyCase(mSharedKeyValueStore.keysAsStream(), null, null, "key1");
    }

    @Test
    public void test_RemoveAsyncHappyCase() {
        mSharedKeyValueStore.put("key1", "value1").put("key2", "value2").commit();
        assertObservableHappyCase(mSharedKeyValueStore.removeAsync("key1"), mPerformMeOnScheduler,
                mNotifyMeOnScheduler, true);
        assertObservableContainsAll(mSharedKeyValueStore.keysAsStream(), null, null, "key2");
    }

    @Test
    public void test_ClearAsyncHapyCase() {
        mSharedKeyValueStore.put("key1", "value1").put("key2", "value2").commit();
        assertObservableHappyCase(mSharedKeyValueStore.clearAsync(), mPerformMeOnScheduler,
                mNotifyMeOnScheduler, true);
        assertObservableHappyCase(mSharedKeyValueStore.keysAsStream(), null, null);
    }

    @Test
    public void test_CommitAsyncHapyCase() {
        Long longValue = 0L;
        Integer intValue = 0;
        Float floatValue = 0.0f;
        Boolean booleanValue = false;
        String stringValue = "value";

        mSharedKeyValueStore.put("long", longValue)
                .put("int", intValue)
                .put("float", floatValue)
                .put("boolean", booleanValue)
                .put("string", stringValue);
        assertObservableHappyCase(mSharedKeyValueStore.keysAsStream(), null, null);

        assertObservableHappyCase(mSharedKeyValueStore.commitAsync(), mPerformMeOnScheduler,
                mNotifyMeOnScheduler, true);
        assertObservableContainsAll(mSharedKeyValueStore.keysAsStream(), null, null, "long",
                "int", "float", "boolean", "string");
    }

    @Test
    public void test_GetOrError_ShouldReturnCorrectError_WhenThrowIfSpecified() {
        mSharedKeyValueStore.put("key1", "value1").put("key2", "value2").commit();
        assertObservableSadCase(mSharedKeyValueStore.getOrError("key1", "value1",
                new InternalErrorException(
                        "key value can't be"
                                + "value1")), null,
                null, InternalErrorException.class);
    }

    @Test
    public void testWriteToPreferences() throws Exception {
        Long longValue = 0L;
        Integer intValue = 0;
        Float floatValue = 0.0f;
        Boolean booleanValue = false;
        String stringValue = "value";

        mSharedKeyValueStore.put("long", longValue)
                .put("int", intValue)
                .put("float", floatValue)
                .put("boolean", booleanValue)
                .put("string", stringValue)
                .commit();
        assertObservableContainsAll(Observable.from(mSharedPreferences.getAll().values()), null,
                null, longValue, intValue, floatValue, booleanValue,
                stringValue);
    }

    @Test
    public void test_GetAsJsonObject_HappyCase() throws Exception {
        String jsonStringOne = "{\"foo\":\"bar\"}";
        JsonObject jsonObjectOne =
                mSharedKeyValueStore.getJsonSerializer().deserialize(jsonStringOne);

        mSharedKeyValueStore.put("keyOne", jsonStringOne).commit();
        JsonObject defaultValue = new JsonObject();

        assertThat(
                mSharedKeyValueStore.getAsOrDefault("keyOne", JsonObject.class, defaultValue),
                is(jsonObjectOne));

        //Now remove the key
        mSharedKeyValueStore.remove("keyOne").commit();

        //And verify if the default value has been returned.
        assertThat(
                mSharedKeyValueStore.getAsOrDefault("keyOne", JsonObject.class, defaultValue),
                is(defaultValue));
    }

    @Test
    public void test_GetAsJsonArray_HappyCase() throws Exception {
        String jsonStringOne = "{\"foo\":\"bar\"}";
        String jsonStringTwo = "{\"bar\":\"foo\"}";
        JsonObject jsonObjectOne =
                mSharedKeyValueStore.getJsonSerializer().deserialize(jsonStringOne);
        JsonObject jsonObjectTwo =
                mSharedKeyValueStore.getJsonSerializer().deserialize(jsonStringTwo);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonObjectOne);
        jsonArray.add(jsonObjectTwo);

        mSharedKeyValueStore.put("keyOne",
                mSharedKeyValueStore.getJsonSerializer().serialize(jsonArray))
                .commit();
        JsonArray defaultValue = new JsonArray();
        assertObservableContainsAll(Observable.just(
                mSharedKeyValueStore.getAsOrDefault("keyOne", JsonArray.class, defaultValue))
                , null, null, jsonArray);

        //Now remove the key
        mSharedKeyValueStore.remove("keyOne").commit();

        //And verify if the default value has been returned.
        assertObservableContainsAll(Observable.just(
                mSharedKeyValueStore.getAsOrDefault("keyOne", JsonArray.class, defaultValue))
                , null, null, defaultValue);
    }
}
