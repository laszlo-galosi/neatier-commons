package com.neatier.repository.repository.datasource;

import com.fernandocejas.arrow.collections.Lists;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.neatier.commons.helpers.JsonSerializer;
import com.neatier.data.entity.OnDeviceKeyTypedValueStorage;
import com.neatier.data.entity.PreferencesTypedValueKeyStorage;
import com.neatier.data.entity.TestEntity;
import com.neatier.repository.DataTestCase;
import com.neatier.repository.datasource.AsyncCacheDataSource;
import com.neatier.repository.entity.TestAutoValueAdapterFactory;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author LargerLife
 * @since 12/06/16.
 */
public class AsyncCacheDataSourceTest extends DataTestCase {

    OnDeviceKeyTypedValueStorage<Integer, TestEntity> onDeviceKeyStorage;
    private AsyncCacheDataSource<Integer, TestEntity> asyncCacheDataSource;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        mGson = new GsonBuilder()
              .registerTypeAdapterFactory(new TestAutoValueAdapterFactory())
              .create();
        onDeviceKeyStorage = new PreferencesTypedValueKeyStorage<Integer, TestEntity>(
              mContext, "ASYNC_TEST", "test_", mGson) {
            @Override public TypeAdapter<TestEntity> getTypeAdapter() {
                return mGson.getAdapter(TestEntity.class);
            }

            @Override public Class<Integer> getKeyClass() {
                return Integer.class;
            }
        };
        mJsonSerializer = new JsonSerializer<>(mGson, new JsonParser());
        asyncCacheDataSource = new AsyncCacheDataSource<>(onDeviceKeyStorage);
    }

    @After
    public void tearDown() {
        onDeviceKeyStorage.clear();
    }

    @Test
    public void getAll_ShouldReturnEmpty_WhenNoAddPerformed() throws Exception {
        assertObservableHappyCase(onDeviceKeyStorage.keys(), null, null);
        assertObservableHappyCase(asyncCacheDataSource.getAllAsync(), null, null);
    }

    @Test
    public void addOrUpdate_ShouldContainsThatElement() throws Exception {
        TestEntity entityOne = onDeviceKeyStorage.getTypeAdapter().fromJson(JSON_ENTITY_1);
        //adding one entity and checking updated entity.
        assertObservableHappyCase(asyncCacheDataSource.addOrUpdateAsync(entityOne), null, null,
                                  entityOne);

        assertThat(asyncCacheDataSource.isValid(entityOne)).isTrue();
        assertObservableHappyCase(onDeviceKeyStorage.keys(), null, null, FAKE_CHANNEL_ID_1);
        assertObservableHappyCase(asyncCacheDataSource.getAllAsync(), null, null, entityOne);
    }

    @Test
    public void addOrUpdate_ShouldUpdateThatItem() throws Exception {
        TestEntity entityOne = onDeviceKeyStorage.getTypeAdapter().fromJson(JSON_ENTITY_1);
        //adding one entity and checking updated entity.
        assertObservableHappyCase(asyncCacheDataSource.addOrUpdateAsync(entityOne), null, null,
                                  entityOne);

        TestEntity entityUpdated = TestEntity.with(FAKE_CHANNEL_ID_1, FAKE_CHANNEL_NAME_2);
        assertObservableHappyCase(asyncCacheDataSource.addOrUpdateAsync(entityUpdated), null, null,
                                  entityUpdated);
        assertThat(asyncCacheDataSource.isValid(entityOne)).isTrue();
        List<TestEntity>
              cachedEntities =
              getObservableEvents(asyncCacheDataSource.getByKeyAsync(FAKE_CHANNEL_ID_1), null, null,
                                  1);
        assertThat(cachedEntities.size()).isEqualTo(1);
        assertThat(cachedEntities.get(0)).isNotEqualTo(entityOne);
        assertThat(cachedEntities.get(0)).isEqualTo(entityUpdated);
    }

    @Test
    public void addOrUpdateAll_ShouldContainsCollectionOfElements() throws Exception {
        TestEntity entityOne = onDeviceKeyStorage.getTypeAdapter().fromJson(JSON_ENTITY_1);
        TestEntity entityTwo = onDeviceKeyStorage.getTypeAdapter().fromJson(JSON_ENTITY_2);
        //adding one entity and checking updated entity.
        assertListObservableHappyCase(asyncCacheDataSource.addOrUpdateAllAsync(
              Lists.newArrayList(entityOne, entityTwo)), null, null,
                                      entityOne, entityTwo);

        assertThat(asyncCacheDataSource.isValid(entityOne)).isTrue();
        assertThat(asyncCacheDataSource.isValid(entityTwo)).isTrue();
        assertObservableContainsAll(onDeviceKeyStorage.keys(), null, null,
                                    FAKE_CHANNEL_ID_1, FAKE_CHANNEL_ID_2);
        assertObservableContainsAll(asyncCacheDataSource.getAllAsync(), null, null, entityOne,
                                    entityTwo);
    }

    @Test
    public void getByKey_ShouldReturnCorrectItem() throws Exception {
        TestEntity entityOne = onDeviceKeyStorage.getTypeAdapter().fromJson(JSON_ENTITY_1);
        TestEntity entityTwo = onDeviceKeyStorage.getTypeAdapter().fromJson(JSON_ENTITY_2);
        //adding one entity and checking updated entity.
        assertListObservableHappyCase(asyncCacheDataSource.addOrUpdateAllAsync(
              Lists.newArrayList(entityOne, entityTwo)), null, null,
                                      entityOne, entityTwo);

        assertObservableHappyCase(asyncCacheDataSource.getByKeyAsync(FAKE_CHANNEL_ID_1), null, null,
                                  entityOne);
        assertObservableHappyCase(asyncCacheDataSource.getByKeyAsync(FAKE_CHANNEL_ID_2), null, null,
                                  entityTwo);
    }

    @Test
    public void deleteByKey_ShouldRemoveItem() throws Exception {
        TestEntity entityOne = onDeviceKeyStorage.getTypeAdapter().fromJson(JSON_ENTITY_1);
        //adding one entity and checking updated entity.
        assertObservableHappyCase(asyncCacheDataSource.addOrUpdateAsync(entityOne), null, null,
                                  entityOne);

        assertObservableHappyCase(asyncCacheDataSource.deleteByKeyAsync(FAKE_CHANNEL_ID_1), null,
                                  null,
                                  Boolean.TRUE);
        assertThat(asyncCacheDataSource.isValid(entityOne)).isFalse();
    }

    @Test
    public void deleteAll_ShouldRemoveAllItems() throws Exception {
        TestEntity entityOne = onDeviceKeyStorage.getTypeAdapter().fromJson(JSON_ENTITY_1);
        TestEntity entityTwo = onDeviceKeyStorage.getTypeAdapter().fromJson(JSON_ENTITY_2);
        //adding one entity and checking updated entity.
        assertListObservableHappyCase(asyncCacheDataSource.addOrUpdateAllAsync(
              Lists.newArrayList(entityOne, entityTwo)), null, null,
                                      entityOne, entityTwo);

        assertObservableHappyCase(asyncCacheDataSource.deleteAllAsync(), null, null, Boolean.TRUE);
        assertThat(asyncCacheDataSource.isValid(entityOne)).isFalse();
        assertThat(asyncCacheDataSource.isValid(entityTwo)).isFalse();
        assertObservableHappyCase(asyncCacheDataSource.getAllAsync(), null, null);
    }
}
