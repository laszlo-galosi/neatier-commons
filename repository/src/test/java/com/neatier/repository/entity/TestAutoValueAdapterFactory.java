package com.neatier.repository.entity;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.neatier.data.entity.AutoGson;
import com.neatier.data.entity.TestEntity;

/**
 * @author László Gálosi
 * @since 16/06/16
 */
public class TestAutoValueAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings("unchecked")
    @Override public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();
        if (!rawType.isAnnotationPresent(AutoGson.class)) {
            return null;
        }

        String packageName = rawType.getPackage().getName();
        String className = rawType.getName().substring(packageName.length() + 1).replace('$', '_');
        String autoValueName = packageName + ".AutoValue_" + className;

        try {
            if (rawType.equals(TestEntity.class)) {
                return (TypeAdapter<T>) TestEntity.typeAdapter(gson);
            } else {
                Class<?> autoValueType = Class.forName(autoValueName);
                return (TypeAdapter<T>) gson.getAdapter(autoValueType);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not load AutoValue type " + autoValueName, e);
        }
    }
}
