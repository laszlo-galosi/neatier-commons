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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fernandocejas.arrow.collections.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LinkedTreeMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;

/**
 * Class  Serializer/Deserializer for basic entities of generic type.
 */
public class JsonSerializer<T> {

    private final Gson gson;
    private JsonParser jsonParser;

    public static
    @Nullable
    <M extends Object> M getAsChecked(String memberName,
                                      final JsonObject jsonObject,
                                      final Class<M> returnClass) {
        if (jsonObject != null && jsonObject.has(memberName)) {
            if (returnClass == JsonObject.class && jsonObject.get(memberName).isJsonObject()) {
                return (M) jsonObject.get(memberName).getAsJsonObject();
            } else if (returnClass == JsonArray.class && jsonObject.get(memberName).isJsonArray()) {
                return (M) jsonObject.get(memberName).getAsJsonArray();
            } else if (jsonObject.get(memberName).isJsonNull()) {
                return (M) jsonObject.get(memberName).getAsJsonNull();
            } else if (jsonObject.get(memberName).isJsonPrimitive()) {
                if (returnClass == String.class) {
                    return (M) jsonObject.get(memberName).getAsString();
                } else if (returnClass == Integer.class) {
                    return (M) Integer.valueOf(jsonObject.get(memberName).getAsInt());
                } else if (returnClass == Long.class) {
                    return (M) Long.valueOf(jsonObject.get(memberName).getAsLong());
                } else if (returnClass == Float.class) {
                    return (M) Float.valueOf(jsonObject.get(memberName).getAsFloat());
                } else if (returnClass == Double.class) {
                    return (M) Double.valueOf(jsonObject.get(memberName).getAsDouble());
                } else if (returnClass == Boolean.class) {
                    return (M) Boolean.valueOf(jsonObject.get(memberName).getAsBoolean());
                }
            }
            return (M) jsonObject.get(memberName);
        }
        return null;
    }

    public JsonSerializer() {
        this(new Gson(), new JsonParser());
    }

    public JsonSerializer(final Gson gson, JsonParser parser) {
        this.gson = gson;
        this.jsonParser = parser;
    }

    public String serialize(final Object value) {
        JsonObject jsonObject = null;
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof List) {
            return gson.toJson(value);
        } else if (value instanceof LinkedTreeMap) {
            LinkedTreeMap treeMap = (LinkedTreeMap) value;
            return gson.toJson(treeMap);
        } else if (value instanceof JsonObject) {
            return gson.toJson((JsonObject) value);
        } else if (value instanceof JsonElement) {
            return gson.toJson((JsonElement) value);
        }
        throw new IllegalArgumentException(
                String.format("Invalid object type to serialize: %s", value));
    }

    /**
     * Serialize an object to Json.
     *
     * @param entity to serialize.
     */
    public String serialize(T entity, Class<T> entityClass) {
        String jsonString = gson.
                                      toJson(entity, entityClass);
        return jsonString;
    }

    /**
     * Serialize an object to Json.
     *
     * @param entityList to serialize.
     */
    public String serializeAll(List<T> entityList, Class<T> entityClass) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0, len = entityList.size(); i < len; i++) {
            jsonArray.add(gson.toJsonTree(entityList.get(i), entityClass));
        }
        String jsonString = gson.toJson(jsonArray);
        return jsonString;
    }

    public Observable<List<T>> serializeAllAsync(String jsonString, final TypeAdapter<T> typeAdapter) {
        JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonString);
        return Observable.from(Lists.newArrayList(jsonArray.iterator()))
                         .flatMap(new Func1<JsonElement, Observable<T>>() {
                             @Override public Observable<T> call(final JsonElement jsonElement) {
                                 return Observable.just(typeAdapter.fromJsonTree(jsonElement));
                             }
                         }).toList();
    }

    public List<T> deserializeAll(String jsonString, Class<T> returnClass) {
        JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonString);
        return fromJsonArray(jsonArray, returnClass);
    }

    public Observable<List<T>> deserializeAllAsync(String jsonString, final TypeAdapter<T> typeAdapter) {
        JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonString);
        return Observable.from(Lists.newArrayList(jsonArray.iterator()))
                  .flatMap(new Func1<JsonElement, Observable<T>>() {
                      @Override public Observable<T> call(final JsonElement jsonElement) {
                          return Observable.just(typeAdapter.fromJsonTree(jsonElement));
                      }
                  }).toList();
    }

    @NonNull public List<T> fromJsonArray(final JsonArray jsonArray, final Class<T> returnClass) {
        int size = jsonArray.size();
        List<T> resultList = new ArrayList<>(size);
        for (int i = 0, len = size; i < len; i++) {
            T elem = gson.fromJson(jsonArray.get(i), returnClass);
            resultList.add(elem);
        }
        return resultList;
    }

    public JsonObject deserialize(final String jsonString) {
        if (jsonString == null) {
            return new JsonObject();
        } else {
            return deserialize(jsonString, JsonObject.class);
        }
    }

    public T fromJsonElement(final JsonElement jsonElement, Class<T> returnClass) {
        return gson.fromJson(jsonElement, returnClass);
    }

    /**
     * Deserialize a json representation of an object.
     *
     * @param jsonString A json string to deserialize.
     * @return the deserialized entity
     */
    public <T> T deserialize(String jsonString, Class<T> entityClass) {
        T entity = gson.fromJson(jsonString, entityClass);
        return entity;
    }

    public <T> T deserialize(final String jsonString, final TypeAdapter<T> typeAdapter)
          throws IOException {
        return typeAdapter.fromJson(jsonString);
    }


    /**
     * Deserialize a json representation of an object.
     *
     * @param treeMap     A treemap deserialize.
     * @param entityClass A json string to deserialize.
     * @return the deserialized entity.
     */
    public <T> T deserialize(LinkedTreeMap treeMap, Class<T> entityClass) {
        int len = treeMap.keySet().size();
        JsonObject jsonObject = new JsonObject();
        for (Object key : treeMap.keySet()) {
            Object value = treeMap.get(key);
            if (value instanceof Number) {
                jsonObject.addProperty((String) key, (Number) value);
            } else if (value instanceof Boolean) {
                jsonObject.addProperty((String) key, (Boolean) value);
            } else if (value instanceof Character) {
                jsonObject.addProperty((String) key, (Character) value);
            } else if (value instanceof String) {
                jsonObject.addProperty((String) key, (String) value);
            }
        }
        T entity = gson.fromJson(jsonObject, entityClass);
        return entity;
    }

    public JsonObject deserialize(LinkedTreeMap treeMap) {
        int len = treeMap.keySet().size();
        JsonObject jsonObject = new JsonObject();
        for (Object key : treeMap.keySet()) {
            Object value = treeMap.get(key);
            if (value instanceof Number) {
                jsonObject.addProperty((String) key, (Number) value);
            } else if (value instanceof Boolean) {
                jsonObject.addProperty((String) key, (Boolean) value);
            } else if (value instanceof Character) {
                jsonObject.addProperty((String) key, (Character) value);
            } else if (value instanceof String) {
                jsonObject.addProperty((String) key, (String) value);
            }
        }
        return jsonObject;
    }

    public boolean isJsonArray(final String jsonString) {
        try {
            JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonString);
            return true;
        } catch (final Exception ex) {
            return false;
        }
    }

    public boolean isJsonObject(final String jsonString) {
        try {
            JsonElement elem = jsonParser.parse(jsonString);
            return elem.isJsonObject() || elem.isJsonArray();
        } catch (final Exception ex) {
            return false;
        }
    }

    public JsonParser getJsonParser() {
        return jsonParser;
    }
}

