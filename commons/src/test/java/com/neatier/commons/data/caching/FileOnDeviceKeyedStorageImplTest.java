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

import org.junit.After;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.io.File;

import rx.Observable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by László Gálosi on 26/07/15
 */
public class FileOnDeviceKeyedStorageImplTest extends CommonsTestCase {

    private static final String KEY_PREFIX = "entity_";
    private static final long FAKE_KEY = 1;
    private static final long FAKE_KEY_2 = 2;
    private FileOnDeviceKeyedStorageImpl<Long, String> mFileOnDeviceKeyedStorage;
    private File cacheDir;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        cacheDir = RuntimeEnvironment.application.getCacheDir();
        mFileOnDeviceKeyedStorage = new FileOnDeviceKeyedStorageImpl<>(cacheDir, KEY_PREFIX);
    }

    @After
    public void tearDown() {
        mFileOnDeviceKeyedStorage.clear();
    }

    @Test
    public void testWriteKeyedContent() {
        String fileContent = "content\n";

        mFileOnDeviceKeyedStorage.writeKeyedContent(FAKE_KEY, fileContent);

        assertThat(mFileOnDeviceKeyedStorage.containsKey(FAKE_KEY), is(true));
    }

    @Test
    public void testKeyedContent() {
        String fileContent = "content\nnewline1\nnewline2";
        mFileOnDeviceKeyedStorage.writeKeyedContent(FAKE_KEY, fileContent);
        String expectedContent = mFileOnDeviceKeyedStorage.readOneByKey(FAKE_KEY);
        assertThat(expectedContent, is(equalTo(fileContent)));
        //Testing overwriting
        fileContent = "content_rewritten\nnewline";
        mFileOnDeviceKeyedStorage.writeKeyedContent(FAKE_KEY, fileContent);
        assertThat(cacheDir.listFiles().length, is(1));
        expectedContent = mFileOnDeviceKeyedStorage.readOneByKey(FAKE_KEY);
        assertThat(expectedContent, is(equalTo(fileContent)));
    }

    @Test
    public void testReadAll() throws Exception {
        mFileOnDeviceKeyedStorage.clear();
        mFileOnDeviceKeyedStorage.writeKeyedContent(FAKE_KEY, "entity1");
        mFileOnDeviceKeyedStorage.writeKeyedContent(FAKE_KEY_2, "entity2");
        Observable<String> actualContents = mFileOnDeviceKeyedStorage.readAll();
        assertObservableHappyCase(actualContents, null, null, "entity1", "entity2");
    }

    @Test
    public void testRemoveOneByKey() throws Exception {
        mFileOnDeviceKeyedStorage.removeOneByKey(FAKE_KEY);
        assertThat(mFileOnDeviceKeyedStorage.containsKey(FAKE_KEY), is(false));
    }

    @Test
    public void testClear() throws Exception {
        String fileContent = "content\n";
        mFileOnDeviceKeyedStorage.writeKeyedContent(FAKE_KEY, fileContent);
        assertThat(mFileOnDeviceKeyedStorage.containsKey(FAKE_KEY), is(true));

        mFileOnDeviceKeyedStorage.clear();
        File[] files = cacheDir.listFiles();
        assertThat(files.length, is(0));
    }

    @Test
    public void testKeys() throws Exception {
        mFileOnDeviceKeyedStorage.clear();
        mFileOnDeviceKeyedStorage.writeKeyedContent(FAKE_KEY, "entity1");
        mFileOnDeviceKeyedStorage.writeKeyedContent(FAKE_KEY_2, "entity2");
        Observable<Long> actualKeys = mFileOnDeviceKeyedStorage.keys();
        assertObservableHappyCase(actualKeys, null, null, FAKE_KEY, FAKE_KEY_2);
    }
}
