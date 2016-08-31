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

package com.neatier.repository.entity;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.neatier.commons.helpers.JsonSerializer;
import com.neatier.data.entity.TestEntity;
import com.neatier.repository.DataTestCase;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by László Gálosi on 08/06/16
 */
public class AutoValueAdapterFactoryTest extends DataTestCase {

    private JsonSerializer mJsonSerializer;
    private Gson mGson;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        mGson = new GsonBuilder()
              .registerTypeAdapterFactory(new TestAutoValueAdapterFactory())
              .create();
        mJsonSerializer = new JsonSerializer(mGson, new JsonParser());
    }

    @Test public void test_SerializeTestEntity_HappyCase() throws Exception {
        TestEntity entityIn = TestEntity.with(FAKE_CHANNEL_ID_1, FAKE_CHANNEL_NAME_1);
        final String json = mJsonSerializer.serialize(entityIn, TestEntity.class);
        TestEntity entityOut =
              (TestEntity) mJsonSerializer.deserialize(json, TestEntity.class);

        assertThat(entityIn, is(entityOut));
    }

    @Test public void test_SerializeTestEntityList_HappyCase() throws Exception {
        TestEntity entityOne = TestEntity.with(FAKE_CHANNEL_ID_1, FAKE_CHANNEL_NAME_1);
        TestEntity entityTwo = TestEntity.with(FAKE_CHANNEL_ID_2, FAKE_CHANNEL_NAME_2);

        String jsonIn = "[" + JSON_ENTITY_1 + "," + JSON_ENTITY_2 + "]";
        String jsonOut = mJsonSerializer.serializeAll(Lists.newArrayList(entityOne, entityTwo),
                                                      TestEntity.class);
        assertThat(jsonOut, is(jsonIn));
    }
}
