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
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import rx.Observable;
import trikita.log.Log;

/**
 * Helper class for serializing/de-serializing  basic objects  with generic type and primitives.
 */
public class JsonSerializer<T> {

    /**
     * Json parser of this serializer.
     */
    public static JsonParser JSON_PARSER = new JsonParser();

    /**
     * The main gson library class.
     */
    private final Gson gson;

    /**
     * JsonParser to parse json strings.
     */
    private JsonParser jsonParser;

    public JsonSerializer() {
        this(new Gson(), new JsonParser());
    }

    /**
     * Constructor with the given gson serializer and a json parser instance
     */
    public JsonSerializer(final Gson gson, JsonParser parser) {
        this.gson = gson;
        this.jsonParser = parser;
    }

    /**
     * Returns a property value with the given member name, of the given {@link JsonObject} with
     * the given type or null if not found or cannot be returned with the given type.
     *
     * @param memberName the property name
     * @param jsonObject the json object which property value should be returned
     * @param returnClass the class of the expected values
     * @param <M> the type of the expected values
     * @see JsonObject#get(String)
     */
    @SuppressWarnings("unchecked")
    public static @Nullable <M> M getAsChecked(String memberName,
          final JsonObject jsonObject, final Class<M> returnClass) {
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

    /**
     * Returns a json object created with the given template string and given key-value pairs
     * parameters.
     *
     * @param params the key-value pairs which entry values will be substituted in the given
     * template in the resulted json object
     * @param templateString the Json serialized template string of the returned json object.
     */
    public static JsonObject buildJsonByTemplate(final KeyValuePairs<String, Object> params,
          final String templateString) {
        JsonObject templateObject = JsonSerializer.JSON_PARSER.parse(templateString)
                                                              .getAsJsonObject();
        JsonObject bodyObject = new JsonObject();
        for (Map.Entry<String, JsonElement> entry :
              templateObject.entrySet()) {
            if (params.containsKey(entry.getKey())) {
                bodyObject.add(entry.getKey(),
                               getCheckedJsonEntry(entry.getKey(), params, templateObject));
            }
        }
        return bodyObject;
    }

    /**
     * Returns a json element which can be JsonObject, JsonArray, JsonPrimitive depending on the
     * given template json object class, from the given key-value parameters by checking if its
     * contains the given key, and its value is
     * type is the same as the template object.
     *
     * @param key the key to be checked if its contained in the given key-value parameters.
     * @param params key-value pairs to be checked containing the given key,
     * and its value is according to the given template json object
     * @param templateObject the template object which class is the expected class of the value
     * contained in the given key-value pairs
     */
    private static JsonElement getCheckedJsonEntry(String key,
          final KeyValuePairs<String, Object> params, final JsonObject templateObject) {
        //String key = entry.getKey();

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

    /**
     * Returns a json element parsed from the given json serialized string.
     *
     * @see JsonParser#parse(String)
     */
    public static JsonElement parse(String jsonString) {
        JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
        jsonReader.setLenient(true);
        return JSON_PARSER.parse(jsonReader);
    }

    /**
     * Return a tree map representation of the given json object.
     */
    @SuppressWarnings("unchecked")
    public static LinkedTreeMap<String, JsonElement> treeMapFromJson(JsonObject json) {
        if (json == null) {
            return null;
        }
        LinkedTreeMap map = new LinkedTreeMap<String, JsonElement>();
        Observable.from(json.entrySet())
                  .subscribe(entry -> {
                      String key = entry.getKey();
                      JsonElement value = entry.getValue();
                      map.put(key, value);
                  });
        return map;
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
            Preconditions.checkArgument(valueElem.isJsonObject(),
                    String.format("%s Not a Json Object: %s", key, params.get(key)));
            return valueElem.getAsJsonObject();
        } else if (templateObject.get(key).isJsonArray()) {
            Preconditions.checkArgument(valueElem.isJsonArray(),
                    String.format("%s Not a JsonArray: %s", key, params.get(key)));
            return valueElem.getAsJsonArray();
        } else if (templateObject.get(key).isJsonPrimitive()) {
            Preconditions.checkArgument(valueElem.isJsonPrimitive(),
                    String.format("%s Not a JsonPrimitive: %s", key, params.get(key)));
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

    /**
     * Returns a serialized string representation of the given serializable  object by examining its
     * type.
     */
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
     * Returns the serialized string representation of the given entity with the given type T.
     */
    public String serialize(T entity, Class<T> entityClass) {
        return gson.toJson(entity, entityClass);
    }

    /**
     * Returns the serialized string representation of the given entity list containing items with
     * the given type T.
     */
    public String serializeAll(List<T> entityList, Class<T> entityClass) {
        JsonArray jsonArray = new JsonArray();
        for (int i = 0, len = entityList.size(); i < len; i++) {
            jsonArray.add(gson.toJsonTree(entityList.get(i), entityClass));
        }
        return gson.toJson(jsonArray);
    }

    /**
     * Returns a Observable emitting a list of the given type adapter type items deserialized from
     * the
     * given json string
     */
    public Observable<List<T>> serializeAllAsync(String jsonString,
          final TypeAdapter<T> typeAdapter) {
        JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonString);
        return Observable.from(Lists.newArrayList(jsonArray.iterator()))
                         .flatMap(
                               jsonElement -> Observable.just(
                                     typeAdapter.fromJsonTree(jsonElement))).toList();
    }

    /**
     * Returns a list of  objects with the given type T, deserialized from the given json string.
     *
     * @see #fromJsonArray(JsonArray, Class)
     */
    public List<T> deserializeAll(String jsonString, Class<T> returnClass) {
        JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonString);
        return fromJsonArray(jsonArray, returnClass);
    }

    public Observable<T> deserializeAllAsync(String jsonString,
          final TypeAdapter<T> typeAdapter) {
        JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonString);
        return Observable.from(Lists.newArrayList(jsonArray.iterator()))
                         .flatMap(jsonElement -> Observable.just(
                                 typeAdapter.fromJsonTree(jsonElement)));
    }

    /**
     * Returns a list of objects with the given type T converted from the given json array.
     *
     * @see Gson#fromJson(JsonElement, Class)
     */
    @NonNull public List<T> fromJsonArray(final JsonArray jsonArray, final Class<T> returnClass) {
        int size = jsonArray.size();
        List<T> resultList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            try {
                T elem = gson.fromJson(jsonArray.get(i), returnClass);
                resultList.add(elem);
            } catch (JsonSyntaxException jse) {
                Log.e("fromJsonArray error", jse);
            }
        }
        return resultList;
    }

    /**
     * Returns a json object deserialized from the given json string.
     *
     * @see #deserialize(String, Class)
     */
    public JsonObject deserialize(final String jsonString) {
        if (jsonString == null) {
            return new JsonObject();
        } else {
            return deserialize(jsonString, JsonObject.class);
        }
    }

    /**
     * Returns an object with the given type E, converted from the given json element.
     *
     * @see Gson#fromJson(JsonElement, Class)
     */
    public T fromJsonElement(final JsonElement jsonElement, Class<T> returnClass) {
        return gson.fromJson(jsonElement, returnClass);
    }

    /**
     * Deserialize the given json serialized string with the given type E.
     *
     * @see Gson#fromJson(Reader, Class)
     */
    public <E> E deserialize(String jsonString, Class<E> entityClass) {
        return gson.fromJson(jsonString, entityClass);
    }

    /**
     * Returns a deserialized object with the given type E deserialized from the given json string.
     *
     * @see TypeAdapter#fromJson(String)
     */
    public <E> E deserialize(final String jsonString, final TypeAdapter<E> typeAdapter)
          throws IOException {
        return typeAdapter.fromJson(jsonString);
    }

    /**
     * Returns an object with the given type converted from the given tree map.
     */
    public <E> E deserialize(LinkedTreeMap treeMap, Class<E> entityClass) {
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
        return gson.fromJson(jsonObject, entityClass);
    }

    /**
     * Returns a json object converted from the given tree map.
     */
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

    /**
     * Returns true if the given json serialized string is a json object.
     *
     * @see JsonElement#isJsonArray()
     */
    public boolean isJsonArray(final String jsonString) {
        try {
            JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonString);
            return true;
        } catch (final Exception ex) {
            return false;
        }
    }

    /**
     * Returns true if the given json serialized string is a json object.
     *
     * @see JsonElement#isJsonObject()
     */
    public boolean isJsonObject(final Object object) {
        try {
            JsonElement elem = jsonParser.parse((String) object);
            return elem.isJsonObject() || elem.isJsonArray();
        } catch (final Exception ex) {
            return false;
        }
    }

    /**
     * Returns true if the given json serialized string is a json primitive.
     *
     * @see JsonElement#isJsonPrimitive()
     */
    public boolean isJsonPrimitive(final Object jsonString) {
        try {
            JsonElement elem = jsonParser.parse((String) jsonString);
            return elem.isJsonPrimitive();
        } catch (final Exception ex) {
            return false;
        }
    }

    /**
     * Returns the json parsers of this serializer.
     */
    public JsonParser getJsonParser() {
        return jsonParser;
    }

    /**
     * Returns the {@link Gson} of this serializer.
     */
    public Gson getGson() {
        return gson;
    }
}

