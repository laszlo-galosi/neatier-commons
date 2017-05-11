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

package com.neatier.commons.helpers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fernandocejas.arrow.collections.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import rx.Observable;
import rx.functions.Func1;
import trikita.log.Log;

/**
 * Class  Serializer/Deserializer for basic entities of generic type.
 */
public class JsonSerializer<T> {

    public static JsonParser JSON_PARSER = new JsonParser();

    private final Gson gson;
    private JsonParser jsonParser;

    public static
    @Nullable <M extends Object> M getAsChecked(String memberName,
          final JsonObject jsonObject,
          final Class<M> returnClass) {
        if (jsonObject != null && jsonObject.has(memberName)) {
            JsonElement jsonElem = jsonObject.get(memberName);
            if (jsonElem.isJsonNull()) {
                return null;
            }
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

    public Observable<List<T>> serializeAllAsync(String jsonString,
          final TypeAdapter<T> typeAdapter) {
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

    public Observable<List<T>> deserializeAllAsync(String jsonString,
          final TypeAdapter<T> typeAdapter) {
        JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonString);
        return Observable.from(Lists.newArrayList(jsonArray.iterator()))
                         .flatMap(jsonElement ->
                                        Observable.just(typeAdapter.fromJsonTree(jsonElement)))
                         .toList();
    }

    @NonNull public List<T> fromJsonArray(final JsonArray jsonArray, final Class<T> returnClass) {
        int size = jsonArray.size();
        List<T> resultList = new ArrayList<>(size);
        for (int i = 0, len = size; i < len; i++) {
            try {
                T elem = gson.fromJson(jsonArray.get(i), returnClass);
                resultList.add(elem);
            } catch (JsonSyntaxException jse) {
                Log.e("fromJsonArray error", jse);
                continue;
            }
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
     * @param treeMap A treemap deserialize.
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

    public boolean isJsonObject(final Object object) {
        try {
            JsonElement elem = jsonParser.parse((String) object);
            return elem.isJsonObject() || elem.isJsonArray();
        } catch (final Exception ex) {
            return false;
        }
    }

    public boolean isJsonPrimitive(final Object jsonString) {
        try {
            JsonElement elem = jsonParser.parse((String) jsonString);
            return elem.isJsonPrimitive();
        } catch (final Exception ex) {
            return false;
        }
    }

    public JsonParser getJsonParser() {
        return jsonParser;
    }

    public static JsonObject buildJsonByTemplate(final KeyValuePairs<String, Object> params,
          final String templateString) {
        JsonObject templateObject = JsonSerializer.JSON_PARSER.parse(templateString)
                                                              .getAsJsonObject();
        JsonObject bodyObject = new JsonObject();
        for (Map.Entry<String, JsonElement> entry :
              templateObject.entrySet()) {
            if (params.containsKey(entry.getKey())) {
                bodyObject.add(entry.getKey(),
                               getCheckedJsonEntry(entry, params, templateObject));
            }
        }
        return bodyObject;
    }

    public static JsonElement getCheckedJsonEntry(final Map.Entry<String, JsonElement> entry,
          final KeyValuePairs<String, Object> params, final JsonObject templateObject) {
        String key = entry.getKey();
        //JsonReader value = new JsonReader(new StringReader((String) params.get(key)));
        //value.setLenient(true);
        Object value = params.get(key);
        JsonElement valueElem = JsonSerializer.parse(value.toString());
        Preconditions.checkNotNull(value, key);
        if (templateObject.get(key).isJsonObject()) {
            Preconditions.checkArgument(
                  valueElem.isJsonObject(),
                  String.format("%s Not a Json Object: %s", key, params.get(key))
            );
            return valueElem.getAsJsonObject();
        } else if (templateObject.get(key).isJsonArray()) {
            Preconditions.checkArgument(
                  valueElem.isJsonArray(),
                  String.format("%s Not a JsonArray: %s", key, params.get(key))
            );
            return valueElem.getAsJsonArray();
        } else if (templateObject.get(key).isJsonPrimitive()) {
            Preconditions.checkArgument(
                  valueElem.isJsonPrimitive(),
                  String.format("%s Not a JsonPrimitive: %s", key, params.get(key))
            );
            if (value instanceof String) {
                return new JsonPrimitive((String) value);
            } else if (value instanceof Long) {
                return new JsonPrimitive((Long) value);
            } else if (value instanceof Number) {
                return new JsonPrimitive((Number) value);
            } else if (value instanceof Boolean) {
                return new JsonPrimitive((Boolean) value);
            }
            return valueElem.getAsJsonPrimitive();
        }
        throw new IllegalArgumentException(
              String.format("%sNot a JsonElement: %s", key, params.get(key)));
    }

    public static JsonElement parse(String jsonString) {
        JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
        jsonReader.setLenient(true);
        return JSON_PARSER.parse(jsonReader);
    }

    public static SortedMap<String, JsonElement> sortedMapFromJson(JsonObject json) {
        if (json == null) {
            return null;
        }
        SortedMap map = new TreeMap();
        Observable.from(json.entrySet())
                  .subscribe(entry -> {
                      String key = entry.getKey();
                      JsonElement value = entry.getValue();
                      map.put(key, value);
                  });
        return map;
    }
}

