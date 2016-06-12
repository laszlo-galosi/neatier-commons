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

package com.neatier.commons.data.caching;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import com.neatier.commons.CommonsTestCase;
import com.neatier.commons.helpers.JsonSerializer;

import org.junit.After;
import org.junit.Test;
import org.robolectric.shadows.ShadowApplication;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by László Gálosi on 26/07/15
 */
public class PreferencesOnDeviceKeyedStorageTest extends CommonsTestCase {

    private static final String PREF_FILE_NAME = "entities";
    private static final String KEY_PREFIX = "entity_";
    private static final long FAKE_KEY = 1;
    private static final String FAKE_NAME = "entity1";
    private static final long FAKE_KEY_2 = 2;
    private static final String FAKE_NAME_2 = "entity2";
    private PreferencesOnDeviceStorage preferencesKeyedStorage;
    private SharedPreferences mSharedPreferences;
    private JsonSerializer mJsonSerializer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        ShadowApplication application = ShadowApplication.getInstance();
        Context context = application.getApplicationContext();
        mSharedPreferences = application.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        mJsonSerializer = new JsonSerializer();
        preferencesKeyedStorage =
                new PreferencesOnDeviceStorage(context, mJsonSerializer, PREF_FILE_NAME, KEY_PREFIX);
    }

    @After
    public void tearDown() {
        preferencesKeyedStorage.clear();
    }

    @Test
    public void testWriteKeyedContent() {
        String content = "content";

        preferencesKeyedStorage.writeKeyedContent(FAKE_KEY, content);
        assertThat(preferencesKeyedStorage.containsKey(FAKE_KEY), is(true));
    }

    @Test
    public void test_WriteJsonObject_HappyCase() throws Exception {
        String jsonStringOne = "{\"foo\":\"bar\"}";
        String jsonStringTwo = "{\"bar\":\"foo\"}";
        JsonObject jsonObjectOne =
                mJsonSerializer.getJsonParser().parse(jsonStringOne).getAsJsonObject();
        preferencesKeyedStorage.writeKeyedContent(FAKE_KEY, jsonObjectOne);
        String key = String.format("%s%s", KEY_PREFIX, FAKE_KEY);
        assertThat(mSharedPreferences.getString(key, null), is(jsonStringOne));
    }

    @Test
    public void test_WriteLinkedTreeMap_HappyCase() throws Exception {
        String jsonStringOne = "{\"foo\":\"bar\"}";
        LinkedTreeMap<String, String> linkedTreeMap = new LinkedTreeMap<>();
        linkedTreeMap.put("foo", "bar");
        preferencesKeyedStorage.writeKeyedContent(FAKE_KEY, linkedTreeMap);
        String key = String.format("%s%s", KEY_PREFIX, FAKE_KEY);
        assertThat(mSharedPreferences.getString(key, null), is(jsonStringOne));
    }

    @Test
    public void test_WriteJsonArray_HappyCase() throws Exception {
        String jsonStringOne = "{\"foo\":\"bar\"}";
        String jsonStringTwo = "{\"bar\":\"foo\"}";
        JsonObject jsonObjectOne =
                mJsonSerializer.getJsonParser().parse(jsonStringOne).getAsJsonObject();
        JsonObject jsonObjectTwo =
                mJsonSerializer.getJsonParser().parse(jsonStringTwo).getAsJsonObject();
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(jsonObjectOne);
        jsonArray.add(jsonObjectTwo);

        preferencesKeyedStorage.writeKeyedContent(FAKE_KEY, jsonArray);
        String key = String.format("%s%s", KEY_PREFIX, FAKE_KEY);
        String expectedContent = String.format("[%s,%s]", jsonStringOne, jsonStringTwo);
        assertThat(mSharedPreferences.getString(key, null), is(expectedContent));
    }

    @Test
    public void test_WriteList_HappyCase() throws Exception {
        String jsonStringOne = "{\"foo\":\"bar\"}";
        String jsonStringTwo = "{\"bar\":\"foo\"}";
        JsonObject jsonObjectOne =
                mJsonSerializer.getJsonParser().parse(jsonStringOne).getAsJsonObject();
        JsonObject jsonObjectTwo =
                mJsonSerializer.getJsonParser().parse(jsonStringTwo).getAsJsonObject();

        preferencesKeyedStorage.writeKeyedContent(FAKE_KEY,
                Arrays.asList(jsonObjectOne, jsonObjectTwo));
        String key = String.format("%s%s", KEY_PREFIX, FAKE_KEY);
        String expectedContent = String.format("[%s,%s]", jsonStringOne, jsonStringTwo);
        assertThat(mSharedPreferences.getString(key, null), is(expectedContent));
    }

    @Test
    public void testKeyedContent() {
        String content = "content";
        preferencesKeyedStorage.writeKeyedContent(FAKE_KEY, content);
        String expectedContent = (String) preferencesKeyedStorage.readOneByKey(FAKE_KEY);
        assertThat(expectedContent, is(equalTo(content)));
        //Testing overwriting
        content = "content_rewritten";
        preferencesKeyedStorage.writeKeyedContent(FAKE_KEY, content);
        assertThat(mSharedPreferences.getAll().size(), is(1));
        expectedContent = (String) preferencesKeyedStorage.readOneByKey(FAKE_KEY);
        assertThat(expectedContent, is(equalTo(content)));
    }

    @Test
    public void testReadAll() throws Exception {
        //Note RoboSharedPreferences.edit.clear() flips a the shoudlClearOnCommit flag which
        // removes the previous content on writing and fails this test.
        // TODD: check Robolectric 3.0+ versions if this is fixed and uncomment the next line.
        // preferencesKeyedStorage.clear();
        preferencesKeyedStorage =
              new PreferencesOnDeviceStorage<Long, String>(mContext, mJsonSerializer, PREF_FILE_NAME, KEY_PREFIX);
        preferencesKeyedStorage.writeKeyedContent(FAKE_KEY, FAKE_NAME);
        preferencesKeyedStorage.writeKeyedContent(FAKE_KEY_2, FAKE_NAME_2);
        assertThat(preferencesKeyedStorage.containsKey(FAKE_KEY), is(true));
        assertThat(preferencesKeyedStorage.containsKey(FAKE_KEY_2), is(true));
        assertThat(mSharedPreferences.getAll().size(), is(2));

        assertThat((String) preferencesKeyedStorage.readOneByKey(FAKE_KEY), is(FAKE_NAME));
        assertThat((String) preferencesKeyedStorage.readOneByKey(FAKE_KEY_2), is(FAKE_NAME_2));

        assertObservableContainsAll(preferencesKeyedStorage.readAll(), null, null,
                FAKE_NAME, FAKE_NAME_2);
    }

    @Test
    public void testRemoveOneByKey() throws Exception {
        preferencesKeyedStorage.removeOneByKey(FAKE_KEY);
        assertThat(preferencesKeyedStorage.containsKey(FAKE_KEY), is(false));
    }

    @Test
    public void testClear() throws Exception {
        String content = "content";
        preferencesKeyedStorage.writeKeyedContent(FAKE_KEY, content);
        assertThat(preferencesKeyedStorage.containsKey(FAKE_KEY), is(true));
        preferencesKeyedStorage.clear();
        assertThat(mSharedPreferences.getAll().size(), is(0));
    }

    @Test
    public void testKeys() throws Exception {
        //Note RoboSharedPreferences.edit.clear() flips a the shoudlClearOnCommit flag which
        // removes the previous content on writing and fails this test.
        // TODD: check Robolectric 3.0+ versions if this is fixed and uncomment the next line.
        // preferencesKeyedStorage.clear();
        preferencesKeyedStorage.writeKeyedContent(FAKE_KEY, "entity1");
        preferencesKeyedStorage.writeKeyedContent(FAKE_KEY_2, "entity2");
        assertThat(preferencesKeyedStorage.containsKey(FAKE_KEY), is(true));
        assertThat(preferencesKeyedStorage.containsKey(FAKE_KEY_2), is(true));
        assertThat(mSharedPreferences.getAll().size(), is(2));

        assertObservableContainsAll(preferencesKeyedStorage.keys(), null, null,
                FAKE_KEY, FAKE_KEY_2);
    }
}
