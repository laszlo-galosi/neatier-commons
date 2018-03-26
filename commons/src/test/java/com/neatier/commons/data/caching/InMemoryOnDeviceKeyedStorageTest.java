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

import com.neatier.commons.CommonsTestCase;
import com.neatier.commons.helpers.KeyValuePairs;
import org.junit.After;
import org.junit.Test;
import rx.Observable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author László Gálosi
 * @since 26/07/15
 */
public class InMemoryOnDeviceKeyedStorageTest extends CommonsTestCase {
    static final String KEY_PREFIX = "entity_";
    private static final long FAKE_KEY = 1;
    private static final long FAKE_KEY_2 = 2;
    private InMemoryOnDeviceKeyedStorage mInMemoryKeyedStorage;
    private KeyValuePairs<Long, String> inMemoryMap;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        inMemoryMap = new KeyValuePairs<>();
        mInMemoryKeyedStorage = new InMemoryOnDeviceKeyedStorage(inMemoryMap);
    }

    @After
    public void tearDown() {
        mInMemoryKeyedStorage.clear();
    }

    @Test
    public void testWriteKeyedContent() {
        String content = "content";

        mInMemoryKeyedStorage.writeKeyedContent(FAKE_KEY, content);
        assertThat(mInMemoryKeyedStorage.containsKey(FAKE_KEY), is(true));
    }

    @Test
    public void testKeyedContent() {
        String content = "content";
        mInMemoryKeyedStorage.writeKeyedContent(FAKE_KEY, content);
        String expectedContent = (String) mInMemoryKeyedStorage.readOneByKey(FAKE_KEY);
        assertThat(expectedContent, is(equalTo(content)));
        //Testing overwriting
        content = "content_rewritten";
        mInMemoryKeyedStorage.writeKeyedContent(FAKE_KEY, content);
        assertThat(inMemoryMap.size(), is(1));
        expectedContent = (String) mInMemoryKeyedStorage.readOneByKey(FAKE_KEY);
        assertThat(expectedContent, is(equalTo(content)));
    }

    @Test
    public void testRemoveOneByKey() throws Exception {
        mInMemoryKeyedStorage.removeOneByKey(FAKE_KEY);
        assertThat(mInMemoryKeyedStorage.containsKey(FAKE_KEY), is(false));
    }

    @Test
    public void testReadAll() throws Exception {
        mInMemoryKeyedStorage.clear();
        mInMemoryKeyedStorage.writeKeyedContent(FAKE_KEY, "entity1");
        mInMemoryKeyedStorage.writeKeyedContent(FAKE_KEY_2, "entity2");
        Observable<String> actualContents = mInMemoryKeyedStorage.readAll();
        assertObservableHappyCase(actualContents, null, null, "entity1", "entity2");
    }

    @Test
    public void testClear() throws Exception {
        String content = "content";
        mInMemoryKeyedStorage.writeKeyedContent(FAKE_KEY, content);
        assertThat(mInMemoryKeyedStorage.containsKey(FAKE_KEY), is(true));
        mInMemoryKeyedStorage.clear();
        assertThat(inMemoryMap.size(), is(0));
    }

    @Test
    public void testKeys() throws Exception {
        mInMemoryKeyedStorage.clear();
        mInMemoryKeyedStorage.writeKeyedContent(FAKE_KEY, "entity1");
        mInMemoryKeyedStorage.writeKeyedContent(FAKE_KEY_2, "entity2");
        Observable<Long> actualKeys = mInMemoryKeyedStorage.keys();
        assertObservableHappyCase(actualKeys, null, null, FAKE_KEY, FAKE_KEY_2);
    }
}
