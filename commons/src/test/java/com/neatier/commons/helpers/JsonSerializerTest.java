/*
 * Copyright (C) 2016 Delight Solutions Ltd., All Rights Reserved
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 *  Proprietary and confidential.
 *
 * All information contained herein is, and remains the property of Delight Solutions Kft.
 *  The intellectual and technical concepts contained herein are proprietary to Delight Solutions
  *  Kft.
 *  and may be covered by U.S. and Foreign Patents, pending patents, and are protected
 *  by trade secret or copyright law. Dissemination of this information or reproduction of
 *  this material is strictly forbidden unless prior written permission is obtained from
 *  Delight Solutions Kft.
 */

package com.neatier.commons.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.neatier.commons.CommonsTestCase;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by László Gálosi on 01/09/16
 */
public class JsonSerializerTest extends CommonsTestCase {

    private static final String FOOBAR_JSON = "{\"foo\":\"bar\", \"bar\":\"foo\" }";
    private static final String FOOBAR_JSONOBJECT_JSON = "{\"foo\" : " + FOOBAR_JSON + ",  \"bar"
          + "\":\"foo\"}";
    private static final String FOOBAR_JSONARRAY_JSON = "{\"foo\" : [\"foo\", \"bar\"],  \"bar"
          + "\":\"foo\"}";

    public static final String PUSH_LINK =
          "edAYAtb5ybs:APA91bEENDCpGFz6x6QN87P0WT3ZoEAJVlN84YeSKW7KllB9RBvSPNUPMfBaypdy0PtcvBvENryhmB3UWeDsXwMVXtk2uDs4alGqt9wrLUc4xqs4P9oUgMHjwPl0jTC5xRsOJwmINW1F";
    public static final String JSREQ_REGISTER_CLIENT =
          "{\"pushlink\" : \"" + PUSH_LINK + "\""
                + "}";
    private Gson mGson;
    private JsonSerializer mJsonSerializer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mGson = new GsonBuilder()/*.setLenient()*/.create();
        mJsonSerializer = new JsonSerializer(mGson, new JsonParser());
    }

    @Test
    public void buildJsonByTemplate_WithSimpleObject_HappyCase() throws Exception {
        KeyValuePairs<String, Object> params = new KeyValuePairs<>()
              .put("foo", "bar")
              .put("bar", "foo");
        JsonObject fooBarObject = JsonSerializer.parse(FOOBAR_JSON).getAsJsonObject();
        JsonObject resultObject = JsonSerializer.buildJsonByTemplate(
              params, FOOBAR_JSON
        );

        assertThat(resultObject).isEqualTo(fooBarObject);

        params.clear().put("pushlink", PUSH_LINK);

        fooBarObject = JsonSerializer.parse(JSREQ_REGISTER_CLIENT).getAsJsonObject();
        resultObject = JsonSerializer.buildJsonByTemplate(
              params, JSREQ_REGISTER_CLIENT
        );

        assertThat(resultObject).isEqualTo(fooBarObject);
    }

    @Test
    public void buildJsonByTemplate_WithJsonObjectAsProperty_HappyCase() throws Exception {
        JsonObject objectProp = new JsonObject();
        objectProp.addProperty("foo", "bar");
        objectProp.addProperty("bar", "foo");
        KeyValuePairs<String, Object> params = new KeyValuePairs<>()
              .put("foo", objectProp)
              .put("bar", "foo");
        JsonObject fooBarObject = new JsonParser().parse(FOOBAR_JSONOBJECT_JSON).getAsJsonObject();
        JsonObject resultObject = JsonSerializer.buildJsonByTemplate(
              params, FOOBAR_JSONOBJECT_JSON
        );

        assertThat(resultObject).isEqualTo(fooBarObject);
    }

    @Test
    public void buildJsonByTemplate_WithJsonArrayAsProperty_HappyCase() throws Exception {
        JsonArray arrayProp = new JsonArray();
        arrayProp.add(new JsonPrimitive("foo"));
        arrayProp.add(new JsonPrimitive("bar"));

        JsonObject objectProp = new JsonObject();
        objectProp.add("foo", arrayProp);
        objectProp.addProperty("bar", "foo");
        KeyValuePairs<String, Object> params = new KeyValuePairs<>()
              .put("foo", arrayProp)
              .put("bar", "foo");
        JsonObject fooBarObject = new JsonParser().parse(FOOBAR_JSONARRAY_JSON).getAsJsonObject();
        JsonObject resultObject = JsonSerializer.buildJsonByTemplate(
              params, FOOBAR_JSONARRAY_JSON
        );

        assertThat(resultObject).isEqualTo(fooBarObject);
    }
}
