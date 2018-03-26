package com.neatier.repository.repository;

import com.fernandocejas.arrow.collections.Lists;
import com.neatier.commons.helpers.KeyValuePairs;
import com.neatier.data.entity.TestEntity;
import com.neatier.repository.AsyncRepository;
import com.neatier.repository.CachePolicy;
import com.neatier.repository.DataTestCase;
import com.neatier.repository.ReadPolicy;
import com.neatier.repository.WritePolicy;
import com.neatier.repository.datasource.AsyncCacheDataSource;
import com.neatier.repository.datasource.AsyncDataSources;
import java.util.ArrayList;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author László Gálosi
 * @since 12/06/16
 */
public class AsyncRepositoryTest extends DataTestCase {
    @Mock AsyncCacheDataSource<Integer, TestEntity> mockCacheDataSource;
    @Mock AsyncCacheDataSource<Integer, TestEntity> mockCacheDataSourceTwo;
    @Mock AsyncDataSources.ReadableAsyncDataSource<Integer, TestEntity> mockRestApiDataSource;
    @Mock AsyncDataSources.ReadableAsyncDataSource<Integer, TestEntity> mockReadableDataSource;
    @Mock AsyncDataSources.WriteableAsyncDataSource<Integer, TestEntity> mockWritableDataSource;
    @Mock AsyncDataSources.WriteableAsyncDataSource<Integer, TestEntity> mockWritableDataSourceTwo;
    @Mock KeyValuePairs<String, Object> mockApiParams;

    AsyncRepository<Integer, TestEntity> asyncRepository;

    @Override public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        asyncRepository = new AsyncRepository<>();
    }

    @Test
    public void getAll_ShouldReturnEmpty_WhenNoDataSourcesAdded() throws Exception {
        assertListObservableHappyCase(asyncRepository.getAll(mockApiParams), null, null);
    }

    @Test
    public void getAll_ShouldReturnEmpty_WhenNoItemAdded() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addReadableDataSources(mockRestApiDataSource);
        given(mockCacheDataSource.getAllAsync()).willReturn(Observable.empty());
        assertListObservableHappyCase(asyncRepository.getAll(mockApiParams), null, null);
    }

    @Test
    public void getByKey_ShouldCacheAndReturnItemFromRestApi_WhenNoItemAdded() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addReadableDataSources(mockRestApiDataSource);
        TestEntity entityOne =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_1, TestEntity.class);
        //RestApi will return this entity.
        given(mockRestApiDataSource.getByKey(FAKE_CHANNEL_ID_1, mockApiParams)).willReturn(
              Observable.just(entityOne));
        //Previously not cached
        given(mockCacheDataSource.getByKeyAsync(FAKE_CHANNEL_ID_1)).willReturn(Observable.empty());
        given(mockCacheDataSource.isValid(entityOne)).willReturn(Boolean.TRUE);

        //Calling getByKey.
        assertObservableHappyCase(asyncRepository.getByKey(FAKE_CHANNEL_ID_1, mockApiParams), null,
                                  null,
                                  entityOne);

        verify(mockCacheDataSource).getByKeyAsync(FAKE_CHANNEL_ID_1);
        verify(mockCacheDataSource).addOrUpdateAsync(entityOne);
        verify(mockRestApiDataSource).getByKey(FAKE_CHANNEL_ID_1, mockApiParams);
        assertObservableHappyCase(asyncRepository.isCached(entityOne), null, null, Boolean.TRUE);
    }

    @Test
    public void getByKey_ShouldNotCacheItemFromRestApi_WhenCacheNeverPolicyIsSet()
          throws Exception {
        asyncRepository.setCachePolicy(CachePolicy.CACHE_NEVER)
                       .setReadPolicy(ReadPolicy.READABLE_ONLY);
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addReadableDataSources(mockRestApiDataSource);
        TestEntity entityOne =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_1, TestEntity.class);
        //RestApi will return this entity.
        given(mockRestApiDataSource.getByKey(FAKE_CHANNEL_ID_1, mockApiParams)).willReturn(
              Observable.just(entityOne));
        //Previously not cached
        given(mockCacheDataSource.getByKeyAsync(FAKE_CHANNEL_ID_1)).willReturn(Observable.empty());

        //Calling getByKey.
        assertObservableHappyCase(asyncRepository.getByKey(FAKE_CHANNEL_ID_1, mockApiParams), null,
                                  null, entityOne);

        verifyZeroInteractions(mockCacheDataSource);
        verify(mockRestApiDataSource).getByKey(FAKE_CHANNEL_ID_1, mockApiParams);
        assertObservableHappyCase(asyncRepository.isCached(entityOne), null, null, Boolean.FALSE);
    }

    @Test
    public void getByKey_ShouldCacheItemFromRestApi_WhenCacheOncePolicyIsSet() throws Exception {
        asyncRepository.setCachePolicy(CachePolicy.CACHE_ONCE);
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        //adding addtional cache
        asyncRepository.addCacheDataSources(mockCacheDataSourceTwo);
        asyncRepository.addReadableDataSources(mockRestApiDataSource);
        TestEntity entityOne =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_1, TestEntity.class);
        //RestApi will return this entity.
        given(mockRestApiDataSource.getByKey(FAKE_CHANNEL_ID_1, mockApiParams)).willReturn(
              Observable.just(entityOne));
        //Previously not cached
        given(mockCacheDataSource.getByKeyAsync(FAKE_CHANNEL_ID_1)).willReturn(Observable.empty());
        given(mockCacheDataSource.isValid(entityOne)).willReturn(Boolean.TRUE);

        //Calling getByKey.
        assertObservableHappyCase(asyncRepository.getByKey(FAKE_CHANNEL_ID_1, mockApiParams), null,
                                  null, entityOne);

        verify(mockCacheDataSource).addOrUpdateAsync(entityOne);
        verify(mockCacheDataSourceTwo, times(0)).addOrUpdateAsync(any(TestEntity.class));
        verify(mockCacheDataSourceTwo).getByKeyAsync(FAKE_CHANNEL_ID_1);
        verify(mockRestApiDataSource).getByKey(FAKE_CHANNEL_ID_1, mockApiParams);
        assertObservableHappyCase(asyncRepository.isCached(entityOne), null, null, Boolean.TRUE);
    }

    @Test
    public void getByKey_ShouldCacheAllItemFromRestApi_WhenCacheAllIsSet() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        //adding addtional cache
        asyncRepository.addCacheDataSources(mockCacheDataSourceTwo);

        asyncRepository.addReadableDataSources(mockRestApiDataSource);
        asyncRepository.setCachePolicy(CachePolicy.CACHE_ALL);
        TestEntity entityOne =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_1, TestEntity.class);
        //RestApi will return this entity.
        given(mockRestApiDataSource.getByKey(FAKE_CHANNEL_ID_1, mockApiParams)).willReturn(
              Observable.just(entityOne));
        //Previously not cached
        given(mockCacheDataSource.getByKeyAsync(FAKE_CHANNEL_ID_1)).willReturn(Observable.empty());

        //Calling getByKey.
        assertObservableHappyCase(asyncRepository.getByKey(FAKE_CHANNEL_ID_1, mockApiParams), null,
                                  null,
                                  entityOne);

        verify(mockCacheDataSource).getByKeyAsync(FAKE_CHANNEL_ID_1);
        verify(mockCacheDataSource).addOrUpdateAsync(entityOne);
        verify(mockCacheDataSourceTwo).getByKeyAsync(FAKE_CHANNEL_ID_1);
        verify(mockCacheDataSourceTwo).addOrUpdateAsync(entityOne);
        verify(mockRestApiDataSource).getByKey(FAKE_CHANNEL_ID_1, mockApiParams);
    }

    @Test
    public void getByKey_ShouldNotReadCacheAndReturnItemFromRestApi_WhenReadableOnly()
          throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addReadableDataSources(mockRestApiDataSource);
        asyncRepository.setReadPolicy(ReadPolicy.READABLE_ONLY);
        TestEntity entityOne =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_1, TestEntity.class);
        //RestApi will return this entity.
        given(mockRestApiDataSource.getByKey(FAKE_CHANNEL_ID_1, mockApiParams)).willReturn(
              Observable.just(entityOne));
        //Previously not cached
        given(mockCacheDataSource.getByKeyAsync(FAKE_CHANNEL_ID_1)).willReturn(Observable.empty());

        //Calling getByKey.
        assertObservableHappyCase(asyncRepository.getByKey(FAKE_CHANNEL_ID_1, mockApiParams), null,
                                  null,
                                  entityOne);

        verify(mockCacheDataSource, times(0)).getByKeyAsync(FAKE_CHANNEL_ID_1);
        verify(mockCacheDataSource).addOrUpdateAsync(entityOne);
        verify(mockRestApiDataSource).getByKey(FAKE_CHANNEL_ID_1, mockApiParams);
    }

    @Test
    public void getAll_ShouldReturnItemsFromRestApi() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addReadableDataSources(mockRestApiDataSource);

        TestEntity entityOne = TestEntity.typeAdapter(mGson).fromJson(JSON_ENTITY_1);
        TestEntity entityTwo = TestEntity.typeAdapter(mGson).fromJson(JSON_ENTITY_2);
        //RestApi will return this entity.
        ArrayList<TestEntity> values = Lists.newArrayList(entityOne, entityTwo);
        given(mockRestApiDataSource.getAll(mockApiParams)).willReturn(
              Observable.just(values));
        //Previously not cached
        given(mockCacheDataSource.getAllAsync()).willReturn(Observable.empty());
        given(mockCacheDataSource.isValid(entityOne)).willReturn(Boolean.TRUE);
        given(mockCacheDataSource.isValid(entityTwo)).willReturn(Boolean.TRUE);

        //Calling getAll.
        assertListObservableHappyCase(asyncRepository.getAll(mockApiParams), null, null,
                                      entityOne, entityTwo);

        assertObservableHappyCase(asyncRepository.areCached(values), null, null, Boolean.TRUE);
        verify(mockCacheDataSource).getAllAsync();
        verify(mockCacheDataSource).addOrUpdateAllAsync(values);
        verify(mockRestApiDataSource).getAll(mockApiParams);
    }

    @Test
    public void getAll_ShouldNotCacheAndReturnItemsFromRestApi_WhenCacheNever() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addReadableDataSources(mockRestApiDataSource);
        asyncRepository.setCachePolicy(CachePolicy.CACHE_NEVER);

        TestEntity entityOne = TestEntity.typeAdapter(mGson).fromJson(JSON_ENTITY_1);
        TestEntity entityTwo = TestEntity.typeAdapter(mGson).fromJson(JSON_ENTITY_2);
        //RestApi will return this entity.
        ArrayList<TestEntity> values = Lists.newArrayList(entityOne, entityTwo);
        given(mockRestApiDataSource.getAll(mockApiParams)).willReturn(
              Observable.just(values));
        //Previously not cached
        given(mockCacheDataSource.getAllAsync()).willReturn(Observable.empty());
        given(mockCacheDataSource.isValid(entityOne)).willReturn(Boolean.FALSE);
        given(mockCacheDataSource.isValid(entityTwo)).willReturn(Boolean.FALSE);

        //Calling getAll.
        assertListObservableHappyCase(asyncRepository.getAll(mockApiParams), null, null,
                                      entityOne, entityTwo);

        verify(mockCacheDataSource).getAllAsync();
        assertObservableHappyCase(asyncRepository.areCached(values), null, null, Boolean.FALSE);
        verifyNoMoreInteractions(mockCacheDataSource);
        verify(mockRestApiDataSource).getAll(mockApiParams);
    }

    @Test
    public void getAll_ShouldReturnItemsFromOneCache() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addReadableDataSources(mockRestApiDataSource);

        TestEntity entityOne =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_1, TestEntity.class);
        TestEntity entityTwo =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_2, TestEntity.class);
        //RestApi will return this entity.
        ArrayList<TestEntity> values = Lists.newArrayList(entityOne, entityTwo);
        given(mockRestApiDataSource.getAll(mockApiParams)).willReturn(
              Observable.just(values));
        //Previously not cached
        given(mockCacheDataSource.getAllAsync()).willReturn(Observable.from(values));

        //Calling getAll.
        assertListObservableHappyCase(asyncRepository.getAll(mockApiParams), null, null,
                                      entityOne, entityTwo);

        verify(mockCacheDataSource).getAllAsync();
        verifyZeroInteractions(mockRestApiDataSource);
    }

    @Test
    public void getAll_ShouldReturnItemsFromMultipleCaches() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addCacheDataSources(mockCacheDataSourceTwo);
        asyncRepository.addReadableDataSources(mockRestApiDataSource);

        TestEntity entityOne =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_1, TestEntity.class);
        TestEntity entityTwo =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_2, TestEntity.class);
        //Previously not cached
        given(mockCacheDataSource.getAllAsync()).willReturn(Observable.just(entityOne));
        given(mockCacheDataSourceTwo.getAllAsync()).willReturn(Observable.just(entityTwo));

        //Calling getAll.
        assertListObservableHappyCase(asyncRepository.getAll(mockApiParams), null, null,
                                      entityOne, entityTwo);

        verify(mockCacheDataSource).getAllAsync();
        verify(mockCacheDataSourceTwo).getAllAsync();
        verifyZeroInteractions(mockRestApiDataSource);
    }

    @Test
    public void getAll_ShouldReturnItemsFromMultipleReadables() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addReadableDataSources(mockRestApiDataSource);
        asyncRepository.addReadableDataSources(mockReadableDataSource);

        TestEntity entityOne =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_1, TestEntity.class);
        TestEntity entityTwo =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_2, TestEntity.class);
        //Previously not cached
        given(mockCacheDataSource.getAllAsync()).willReturn(Observable.empty());
        given(mockRestApiDataSource.getAll(mockApiParams)).willReturn(
              Observable.just(Lists.newArrayList(entityOne)));
        given(mockReadableDataSource.getAll(mockApiParams)).willReturn(
              Observable.just(Lists.newArrayList(entityTwo)));

        //Calling getAll.
        assertListObservableHappyCase(asyncRepository.getAll(mockApiParams), null, null,
                                      entityOne, entityTwo);

        verify(mockCacheDataSource).getAllAsync();
        verify(mockRestApiDataSource).getAll(mockApiParams);
        verify(mockReadableDataSource).getAll(mockApiParams);
    }

    @Test
    public void addOrUpdateAll_ShouldWriteAndReturnItems() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addReadableDataSources(mockRestApiDataSource);
        asyncRepository.addWriteableDataSources(mockWritableDataSource);

        TestEntity entityOne =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_1, TestEntity.class);
        TestEntity entityTwo =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_2, TestEntity.class);
        ArrayList<TestEntity> values = Lists.newArrayList(entityOne, entityTwo);

        given(mockWritableDataSource.addOrUpdateAll(values, mockApiParams)).willReturn(
              Observable.just(values));
        given(mockCacheDataSource.addOrUpdateAllAsync(values)).willReturn(Observable.just(values));

        //Calling getAll.
        assertListObservableHappyCase(asyncRepository.addOrUpdateAll(values, mockApiParams), null,
                                      null,
                                      entityOne, entityTwo);
        verify(mockCacheDataSource).addOrUpdateAllAsync(values);
        verify(mockWritableDataSource).addOrUpdateAll(values, mockApiParams);
    }

    @Test
    public void addOrUpdateAll_ShouldWriteMultipleDataSourcesAndReturnItems() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addReadableDataSources(mockRestApiDataSource);
        asyncRepository.addWriteableDataSources(mockWritableDataSource);
        asyncRepository.addWriteableDataSources(mockWritableDataSourceTwo);

        TestEntity entityOne =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_1, TestEntity.class);
        TestEntity entityTwo =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_2, TestEntity.class);
        ArrayList<TestEntity> values = Lists.newArrayList(entityOne, entityTwo);

        given(mockWritableDataSource.addOrUpdateAll(values, mockApiParams)).willReturn(
              Observable.just(values));
        given(mockWritableDataSourceTwo.addOrUpdateAll(values, mockApiParams)).willReturn(
              Observable.just(values));
        given(mockCacheDataSource.addOrUpdateAllAsync(values)).willReturn(Observable.just(values));

        //Calling getAll.
        assertListObservableHappyCase(asyncRepository.addOrUpdateAll(values, mockApiParams), null,
                                      null,
                                      entityOne, entityTwo);

        verify(mockCacheDataSource).addOrUpdateAllAsync(values);
        verify(mockWritableDataSource).addOrUpdateAll(values, mockApiParams);
        verify(mockWritableDataSourceTwo).addOrUpdateAll(values, mockApiParams);
    }

    @Test
    public void addOrUpdate_ShouldWriteAndReturnItem() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addWriteableDataSources(mockWritableDataSource);

        TestEntity entityOne =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_1, TestEntity.class);
        given(mockWritableDataSource.addOrUpdate(entityOne)).willReturn(Observable.just(entityOne));
        given(mockCacheDataSource.addOrUpdateAsync(entityOne)).willReturn(
              Observable.just(entityOne));

        //Calling getAll.
        assertObservableHappyCase(asyncRepository.addOrUpdate(entityOne), null, null, entityOne);

        verify(mockWritableDataSource).addOrUpdate(entityOne);
    }

    @Test
    public void addOrUpdate_ShouldWriteMultipleDataSourcesAndReturnItem() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addWriteableDataSources(mockWritableDataSource);
        asyncRepository.addWriteableDataSources(mockWritableDataSourceTwo);

        TestEntity entityOne =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_1, TestEntity.class);

        given(mockWritableDataSource.addOrUpdate(entityOne)).willReturn(Observable.just(entityOne));
        given(mockWritableDataSourceTwo.addOrUpdate(entityOne)).willReturn(
              Observable.just(entityOne));
        given(mockCacheDataSource.addOrUpdateAsync(entityOne)).willReturn(
              Observable.just(entityOne));

        //Calling getAll.
        assertObservableHappyCase(asyncRepository.addOrUpdate(entityOne), null, null, entityOne);

        verify(mockWritableDataSource).addOrUpdate(entityOne);
        verify(mockWritableDataSourceTwo).addOrUpdate(entityOne);
        verify(mockCacheDataSource).addOrUpdateAsync(entityOne);
    }

    @Test
    public void addOrUpdate_ShouldWriteOnceAndReturnItems_WhenWriteOncePolicy() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addWriteableDataSources(mockWritableDataSource);
        asyncRepository.addWriteableDataSources(mockWritableDataSourceTwo);

        asyncRepository.setWritePolicy(WritePolicy.WRITE_ONCE);

        TestEntity entityOne =
              (TestEntity) mJsonSerializer.deserialize(JSON_ENTITY_1, TestEntity.class);

        given(mockWritableDataSource.addOrUpdate(entityOne)).willReturn(Observable.just(entityOne));
        given(mockWritableDataSourceTwo.addOrUpdate(entityOne)).willReturn(
              Observable.just(entityOne));
        given(mockCacheDataSource.addOrUpdateAsync(entityOne)).willReturn(
              Observable.just(entityOne));

        //Calling getAll.
        assertObservableHappyCase(asyncRepository.addOrUpdate(entityOne), null, null, entityOne);

        verify(mockWritableDataSource).addOrUpdate(entityOne);
        verify(mockCacheDataSource).addOrUpdateAsync(entityOne);
        verifyZeroInteractions(mockWritableDataSourceTwo);
    }

    @Test
    public void deleteByKey_ShouldRemoveFromMultipleWritableDataSource() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addCacheDataSources(mockCacheDataSourceTwo);
        asyncRepository.addWriteableDataSources(mockWritableDataSource);
        asyncRepository.addWriteableDataSources(mockWritableDataSourceTwo);

        given(mockWritableDataSource.deleteByKey(FAKE_CHANNEL_ID_1, mockApiParams)).willReturn(
              Observable.just(Boolean.TRUE));
        given(mockWritableDataSourceTwo.deleteByKey(FAKE_CHANNEL_ID_1, mockApiParams)).willReturn(
              Observable.just(Boolean.TRUE));
        given(mockCacheDataSource.deleteByKeyAsync(FAKE_CHANNEL_ID_1)).willReturn(Observable.just
              (Boolean.TRUE));
        given(mockCacheDataSourceTwo.deleteByKeyAsync(FAKE_CHANNEL_ID_1)).willReturn(Observable
                                                                                           .just(Boolean.TRUE));

        //Calling getAll.
        assertObservableHappyCase(asyncRepository.deleteByKey(FAKE_CHANNEL_ID_1, mockApiParams),
                                  null, null, Boolean.TRUE);

        verify(mockWritableDataSource).deleteByKey(FAKE_CHANNEL_ID_1, mockApiParams);
        verify(mockWritableDataSourceTwo).deleteByKey(FAKE_CHANNEL_ID_1, mockApiParams);
        verify(mockCacheDataSource).deleteByKeyAsync(FAKE_CHANNEL_ID_1);
        verify(mockCacheDataSourceTwo).deleteByKeyAsync(FAKE_CHANNEL_ID_1);
    }

    @Test
    public void deleteByKey_ShouldRemoveOnce_WhenWriteOnceIsSet() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addWriteableDataSources(mockWritableDataSource);
        asyncRepository.addWriteableDataSources(mockWritableDataSourceTwo);

        asyncRepository.setWritePolicy(WritePolicy.WRITE_ONCE);

        given(mockWritableDataSource.deleteByKey(FAKE_CHANNEL_ID_1, mockApiParams)).willReturn(
              Observable.just(Boolean.TRUE));
        given(mockWritableDataSourceTwo.deleteByKey(FAKE_CHANNEL_ID_1, mockApiParams)).willReturn(
              Observable.just(Boolean.TRUE));
        given(mockCacheDataSource.deleteByKeyAsync(FAKE_CHANNEL_ID_1)).willReturn(
              Observable.just(Boolean.TRUE));

        //Calling getAll.
        assertObservableHappyCase(asyncRepository.deleteByKey(FAKE_CHANNEL_ID_1, mockApiParams),
                                  null, null, Boolean.TRUE);

        verify(mockWritableDataSource).deleteByKey(FAKE_CHANNEL_ID_1, mockApiParams);
        verifyZeroInteractions(mockWritableDataSourceTwo);
        verify(mockCacheDataSource).deleteByKeyAsync(FAKE_CHANNEL_ID_1);
    }

    @Test
    public void deleteAll_ShouldRemoveFromMultipleWritableDataSource() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addCacheDataSources(mockCacheDataSourceTwo);
        asyncRepository.addWriteableDataSources(mockWritableDataSource);
        asyncRepository.addWriteableDataSources(mockWritableDataSourceTwo);

        given(mockWritableDataSource.deleteAll(mockApiParams)).willReturn(
              Observable.just(Boolean.TRUE));
        given(mockWritableDataSourceTwo.deleteAll(mockApiParams)).willReturn(
              Observable.just(Boolean.TRUE));
        given(mockCacheDataSource.deleteAllAsync()).willReturn(Observable.just(Boolean.TRUE));
        given(mockCacheDataSourceTwo.deleteAllAsync()).willReturn(Observable.just(Boolean.TRUE));

        //Calling getAll.
        assertObservableHappyCase(asyncRepository.deleteAll(mockApiParams), null, null,
                                  Boolean.TRUE);

        verify(mockWritableDataSource).deleteAll(mockApiParams);
        verify(mockWritableDataSourceTwo).deleteAll(mockApiParams);
        verify(mockCacheDataSource).deleteAllAsync();
        verify(mockCacheDataSourceTwo).deleteAllAsync();
    }

    @Test
    public void deleteAll_ShouldRemoveOnce_WhenWriteOnceIsSet() throws Exception {
        asyncRepository.addCacheDataSources(mockCacheDataSource);
        asyncRepository.addWriteableDataSources(mockWritableDataSource);
        asyncRepository.addWriteableDataSources(mockWritableDataSourceTwo);

        asyncRepository.setWritePolicy(WritePolicy.WRITE_ONCE);

        given(mockWritableDataSource.deleteAll(mockApiParams)).willReturn(
              Observable.just(Boolean.TRUE));
        given(mockWritableDataSourceTwo.deleteAll(mockApiParams)).willReturn(
              Observable.just(Boolean.TRUE));
        given(mockCacheDataSource.deleteAllAsync()).willReturn(Observable.just(Boolean.TRUE));

        //Calling getAll.
        assertObservableHappyCase(asyncRepository.deleteAll(mockApiParams), null, null,
                                  Boolean.TRUE);

        verify(mockWritableDataSource).deleteAll(mockApiParams);
        verifyZeroInteractions(mockWritableDataSourceTwo);
        verify(mockCacheDataSource).deleteAllAsync();
    }
}
