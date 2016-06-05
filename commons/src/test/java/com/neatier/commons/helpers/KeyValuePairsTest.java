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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;

import com.google.gson.annotations.SerializedName;
import com.neatier.commons.ApplicationStub;
import com.neatier.commons.BuildConfig;
import com.neatier.commons.CommonsTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import rx.Observable;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by László Gálosi on 30/07/15
 */
@Config(sdk = 16, constants = BuildConfig.class, application = ApplicationStub.class, manifest =
        Config.NONE)
public class KeyValuePairsTest extends CommonsTestCase {

    private static final String PREF_FILE_NAME = "entities";
    private static final String FOOBAR_JSON = "{\"foo\":\"bar\", \"bar\":\"foo\" }";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    SharedKeyValueStore<String, String> mKeyValueStore;
    private SharedPreferences mSharedPreferences;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        ShadowApplication application = ShadowApplication.getInstance();
        Context context = application.getApplicationContext();
        mSharedPreferences = application.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        mKeyValueStore = new SharedKeyValueStore<>(context, PREF_FILE_NAME);
    }

    @After
    public void tearDown() throws Exception {
        mSharedPreferences.edit().clear().commit();
    }

    @Test
    public void testKeysAsStream() throws Exception {
        KeyValuePairs<String, String> keyValuePairs =
                new KeyValuePairs<>().put("key1", "value1").put("key2", "value2");
        assertObservableHappyCase(keyValuePairs.keysAsStream(), null, null, "key1", "key2");
    }

    @Test
    public void testKeysAsListStream() throws Exception {
        KeyValuePairs<String, String> keyValuePairs =
                new KeyValuePairs<>().put("key1", "value1").put("key2", "value2");

        assertListObservableHappyCase(keyValuePairs.keysAsListStream(), null, null, "key1", "key2");
    }

    public void testValuesAsStream() throws Exception {
        KeyValuePairs<String, String> keyValuePairs =
                new KeyValuePairs<>().put("key1", "value1").put("key2", "value2");
        assertObservableHappyCase(keyValuePairs.valuesAsStream(), null, null, "value1", "value2");
    }

    public void testValuesAsListStream() throws Exception {
        KeyValuePairs<String, String> keyValuePairs =
                new KeyValuePairs<>().put("key1", "value1").put("key2", "value2");
        assertListObservableHappyCase(keyValuePairs.valuesAsListStream(), null, null, "value1",
                "value2");
    }

    @Test
    public void testFromPreferences() throws Exception {
        mSharedPreferences.edit().putString("key1", "value1").putString("key2", "value2").commit();
        KeyValuePairs<String, String> keyValuePairs = KeyValuePairs.from(mSharedPreferences);
        KeyValuePairs<String, String> expectedValuePairs =
                new KeyValuePairs<>().put("key1", "value1").put("key2", "value2");
        assertThat(keyValuePairs, is(expectedValuePairs));
    }

    @Test
    public void testFromSharedKeyStoreAsync() throws Exception {

        mKeyValueStore.putString("key1", "value1").putString("key2", "value2").commit();
        KeyValuePairs<String, String> keyValuePairs = new KeyValuePairs<>();

        assertObservableHappyCase(keyValuePairs.fromAsync(mKeyValueStore), null, null, true);
        KeyValuePairs<String, String> expectedValuePairs =
                new KeyValuePairs<>().put("key1", "value1").put("key2", "value2");
        assertThat(keyValuePairs, is(expectedValuePairs));
    }

    @Test
    public void testFromSparseArray() throws Exception {
        SparseArray<String> sparseArray = new SparseArray<>();
        sparseArray.put(1, "value1");
        sparseArray.put(2, "value2");
        KeyValuePairs<Integer, Object> keyValuePairs = KeyValuePairs.from(sparseArray);
        KeyValuePairs<Integer, Object> expectedValuePairs =
                new KeyValuePairs<>().put(1, "value1").put(2, "value2");
        assertThat(keyValuePairs, is(expectedValuePairs));
    }

    @Test
    public void testFromSparseIntArray() throws Exception {
        SparseIntArray sparseIntArray = new SparseIntArray();
        sparseIntArray.put(1, 1);
        sparseIntArray.put(2, 2);
        KeyValuePairs<Integer, Integer> keyIntValuePairs = KeyValuePairs.from(sparseIntArray);
        KeyValuePairs<Integer, Integer> expectedIntValuePairs =
                new KeyValuePairs<>().put(1, 1).put(2, 2);
        assertThat(keyIntValuePairs, is(expectedIntValuePairs));
    }

    @Test
    public void testFromSparseBooleanArray() throws Exception {
        SparseBooleanArray sparseBoolArray = new SparseBooleanArray();
        sparseBoolArray.put(1, true);
        sparseBoolArray.put(2, false);
        KeyValuePairs<Integer, Boolean> keyBoolValuePairs = KeyValuePairs.from(sparseBoolArray);
        KeyValuePairs<Integer, Boolean> expectedBoolValuePairs =
                new KeyValuePairs<>().put(1, true).put(2, false);
        assertThat(keyBoolValuePairs, is(expectedBoolValuePairs));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Test
    public void testFromSparseLongArray()
            throws Exception {
        expectedException.expect(UnsupportedOperationException.class);
        SparseLongArray sparseLongArray = new SparseLongArray();
        sparseLongArray.put(1, 100L);
        sparseLongArray.put(2, 200L);
        KeyValuePairs<Integer, Long> keyLongValuePairs = KeyValuePairs.from(sparseLongArray);
    }

    @Test
    public void testWriteToPreferences() throws Exception {
        mSharedPreferences.edit().putString("key1", "value1").putString("key2", "value2").commit();
        KeyValuePairs<String, String> keyValuePairs =
                new KeyValuePairs<>().put("key3", "value3").put("key4", "value4");
        keyValuePairs.to(mSharedPreferences);
        Observable<String> result = Observable.from(mSharedPreferences.getAll().keySet());

        assertObservableContainsAll(result, null, null, "key1", "key2", "key3", "key4");

        KeyValuePairs<String, String> keyIntValuePairs =
                new KeyValuePairs<>().put("key3", 3).put("key4", 4);
        keyIntValuePairs.to(mSharedPreferences);

        assertObservableContainsAll(Observable.from(mSharedPreferences.getAll().keySet()), null,
                null,
                "key1", "key2", "key3", "key4");

        assertObservableContainsAll(Observable.from(mSharedPreferences.getAll().values()), null,
                null,
                "value1", "value2", 3, 4);
    }

    @Test
    public void test_JsonSerializedAsyncHappyCase() throws Exception {
        KeyValuePairs<String, String> valuePairs = new KeyValuePairs<>(1).put("json", FOOBAR_JSON);
        FooBar fooBar = new FooBar("bar", "foo");
        JsonSerializer<FooBar> jsonSerializer = new JsonSerializer<>();
        assertObservableHappyCase(
                valuePairs.getJsonSerializedAsync("json", FooBar.class, jsonSerializer), null, null,
                fooBar);
    }

    private class FooBar {
        /**
         * foo : bar
         * bar : foo
         */
        @SerializedName("foo")
        private String foo;
        @SerializedName("bar")
        private String bar;

        public FooBar(final String foo, final String bar) {
            this.foo = foo;
            this.bar = bar;
        }

        public String getFoo() {
            return foo;
        }

        public String getBar() {
            return bar;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof FooBar)) {
                return false;
            }

            FooBar fooBar = (FooBar) o;

            if (foo != null
                    ? !foo.equals(fooBar.foo)
                    : fooBar.foo != null) {
                return false;
            }
            return !(bar != null
                    ? !bar.equals(fooBar.bar)
                    : fooBar.bar != null);
        }

        @Override
        public int hashCode() {
            int result = foo != null
                    ? foo.hashCode()
                    : 0;
            result = 31 * result + (bar != null
                    ? bar.hashCode()
                    : 0);
            return result;
        }
    }
}
