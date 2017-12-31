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

package com.neatier.data.entity;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Test entity annotated with {@link AutoValue @AutoValue} for testing purpose.
 * @author László Gálosi
 * @since 08/06/16
 */
@AutoValue
@AutoGson
public abstract class TestEntity implements Identifiable<Integer> {
    @SerializedName("test_id") public abstract int Id();

    @SerializedName("test_name") public abstract String Name();

    @Override public Integer getKey() {
        return Id();
    }

    public static TypeAdapter<TestEntity> typeAdapter(Gson gson) {
        return new AutoValue_TestEntity.GsonTypeAdapter(gson);
    }

    /**
     * Static factory method for creating new Entity.
     *
     * @param channelId the channel Id
     * @param channelName the channel name
     */
    public static TestEntity with(final int id, final String name) {
        return new AutoValue_TestEntity(id, name);
    }
}
