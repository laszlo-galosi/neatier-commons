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

package com.neatier.repository;

import android.support.annotation.NonNull;
import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.neatier.commons.exception.InternalErrorException;
import com.neatier.commons.helpers.KeyValuePairs;
import com.neatier.commons.helpers.Preconditions;
import com.neatier.data.entity.Identifiable;
import com.neatier.repository.datasource.AsyncCacheDataSource;
import com.neatier.repository.datasource.AsyncDataSources.ReadableAsyncDataSource;
import com.neatier.repository.datasource.AsyncDataSources.WriteableAsyncDataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import rx.Observable;
import trikita.log.Log;

/**
 * Created by László Gálosi on 10/06/16
 */
public class AsyncRepository<K, V extends Identifiable<K>>
      implements ReadableAsyncDataSource<K, V>,
                 WriteableAsyncDataSource<K, V> {

    private final Collection<ReadableAsyncDataSource<K, V>> readableDataSources =
          new LinkedList<>();
    private final Collection<WriteableAsyncDataSource<K, V>> writeableDataSources =
          new LinkedList<>();
    private final Collection<AsyncCacheDataSource<K, V>> cacheDataSources = new LinkedList<>();

    private ReadPolicy mReadPolicy = ReadPolicy.READ_ALL;
    private WritePolicy mWritePolicy = WritePolicy.WRITE_ALL;
    private CachePolicy mCachePolicy = CachePolicy.CACHE_ALL;

    public AsyncRepository() {
        super();
    }

    @SafeVarargs
    public final <R extends ReadableAsyncDataSource<K, V>> void addReadableDataSources(
          R... readableDataSources) {
        this.readableDataSources.addAll(Arrays.asList(readableDataSources));
    }

    @SafeVarargs
    public final <R extends WriteableAsyncDataSource<K, V>> void addWriteableDataSources(
          R... writeableDataSources) {
        this.writeableDataSources.addAll(Arrays.asList(writeableDataSources));
    }

    @SafeVarargs public final <R extends AsyncCacheDataSource<K, V>> void addCacheDataSources(
          R... cacheDataSources) {
        this.cacheDataSources.addAll(Arrays.asList(cacheDataSources));
    }

    @Override public Observable<V> getByKey(K key,
          final KeyValuePairs<String, Object> requestParams) {
        Log.d("getByKeyAsync", key, requestParams);
        return getByKeyAsync(key, mReadPolicy, requestParams);
    }

    @Override public Observable<List<V>> getAll(final KeyValuePairs<String, Object> requestParams) {
        return getAllAsync(mReadPolicy, requestParams);
    }

    @RxLogObservable(RxLogObservable.Scope.NOTHING)
    public Observable<V> getByKeyAsync(K key, ReadPolicy policy,
          final KeyValuePairs<String, Object> requestParams) {
        validateKey(key);
        Log.d("getByKeyAsync", key, policy);
        return Observable.just(policy).flatMap(p -> {
            if (p.useCache()) {
                return getValueFromCaches(key)
                      .switchIfEmpty(
                            Observable.just(p).filter(readPolicy -> p.useReadable())
                                      .flatMap(readPolicy -> getValueFromReadables(key,
                                                                                   requestParams))
                      );
            } else {
                return getValueFromReadables(key, requestParams);
            }
        }).flatMap(value -> {
            try {
                populateCaches(value);
                return Observable.just(value);
            } catch (Exception e) {
                return Observable.error(new InternalErrorException(e));
            }
        });
    }

    @RxLogObservable(RxLogObservable.Scope.NOTHING)
    public Observable<List<V>> getAllAsync(final ReadPolicy policy,
          final KeyValuePairs<String, Object> requestPatams) {
        return Observable.just(policy).flatMap(p -> {
            if (p.useCache()) {
                return getValuesFromCaches()
                      .flatMap(list -> list.isEmpty() ? Observable.empty() : Observable.just(list))
                      .switchIfEmpty(
                            Observable.just(p).filter(readPolicy -> p.useReadable())
                                      .flatMap(readPolicy -> getValuesFromReadables(requestPatams))
                      );
            } else {
                return getValuesFromReadables(requestPatams);
            }
        }).flatMap(values -> {
            try {
                populateCaches(values);
                return Observable.just(values);
            } catch (Exception e) {
                return Observable.error(new InternalErrorException(e));
            }
        });
    }

    @RxLogObservable(RxLogObservable.Scope.NOTHING)
    public Observable<V> addOrUpdate(final V value) {
        return addOrUpdateAsync(value, mWritePolicy);
    }

    @RxLogObservable(RxLogObservable.Scope.NOTHING)
    public Observable<V> addOrUpdateAsync(final V value, final WritePolicy policy) {
        return Observable.from(writeableDataSources)
                         .toList().switchMap(dataSources -> {
                  int len = dataSources.size();
                  if (len > 0 && policy == WritePolicy.WRITE_ONCE) {
                      return Observable.just(dataSources.get(0));
                  } else {
                      return Observable.from(dataSources);
                  }
              }).flatMap(dataSource -> dataSource.addOrUpdate(value))
                         .takeLast(1)
                         .flatMap(v -> {
                             try {
                                 populateCaches(v);
                                 return Observable.just(v);
                             } catch (Exception e) {
                                 return Observable.error(new InternalErrorException(e));
                             }
                         }).takeLast(1);
    }

    @RxLogObservable(RxLogObservable.Scope.NOTHING)
    public Observable<List<V>> addOrUpdateAll(final Collection<V> values,
          final KeyValuePairs<String, Object> requestParams) {
        return addOrUpdateAllAsync(values, mWritePolicy, requestParams);
    }

    @RxLogObservable(RxLogObservable.Scope.NOTHING)
    public Observable<List<V>> addOrUpdateAllAsync(final Collection<V> values,
          final WritePolicy policy, final KeyValuePairs<String, Object> requestParams) {
        return Observable.from(writeableDataSources)
                         .toList().switchMap(dataSources -> {
                  int len = dataSources.size();
                  if (len > 0 && policy == WritePolicy.WRITE_ONCE) {
                      return Observable.just(dataSources.get(0));
                  } else {
                      return Observable.from(dataSources);
                  }
              }).flatMap(dataSource -> dataSource.addOrUpdateAll(values, requestParams))
                         .takeLast(1)
                         .flatMap(v -> {
                             try {
                                 populateCaches(v);
                                 return Observable.just(v);
                             } catch (Exception e) {
                                 return Observable.error(new InternalErrorException(e));
                             }
                         });
    }

    @RxLogObservable(RxLogObservable.Scope.NOTHING)
    public Observable<Boolean> deleteByKey(K key,
          final KeyValuePairs<String, Object> requestParams) {
        return Observable.from(writeableDataSources)
                         .toList().switchMap(dataSources -> {
                  int len = dataSources.size();
                  if (len > 0 && mWritePolicy == WritePolicy.WRITE_ONCE) {
                      return Observable.just(dataSources.get(0));
                  } else {
                      return Observable.from(dataSources);
                  }
              }).flatMap(dataSource -> dataSource.deleteByKey(key, requestParams))
                         .takeLast(1)
                         .flatMap(v -> removeFromCaches(key));
    }

    @RxLogObservable(RxLogObservable.Scope.NOTHING)
    public Observable<Boolean> deleteAll(final KeyValuePairs<String, Object> requestParams) {
        return Observable.from(writeableDataSources)
                         .toList().switchMap(dataSources -> {
                  int len = dataSources.size();
                  if (len > 0 && mWritePolicy == WritePolicy.WRITE_ONCE) {
                      return Observable.just(dataSources.get(0));
                  } else {
                      return Observable.from(dataSources);
                  }
              }).flatMap(dataSource -> dataSource.deleteAll(requestParams))
                         .takeLast(1)
                         .flatMap(v -> clearCaches());
    }

    public AsyncRepository setReadPolicy(final ReadPolicy readPolicy) {
        mReadPolicy = readPolicy;
        return this;
    }

    public AsyncRepository setWritePolicy(final WritePolicy writePolicy) {
        mWritePolicy = writePolicy;
        return this;
    }

    public AsyncRepository setCachePolicy(
          final CachePolicy cachePolicy) {
        mCachePolicy = cachePolicy;
        return this;
    }

    private Observable<V> getValueFromCaches(final K key) {
        Log.d("getValueFromCaches", key);
        return Observable.from(cacheDataSources).flatMap(
              cacheDataSource -> cacheDataSource.getByKeyAsync(key));
    }

    private Observable<List<V>> getValuesFromCaches() {
        Log.d("getValuesFromCaches");
        final List<V> resultList = new ArrayList<>();
        return Observable.from(cacheDataSources)
                         .flatMap(cacheDataSource -> cacheDataSource.getAllAsync())
                         .collect(() -> resultList, (vs, v) -> vs.add(v));
    }

    private Observable<V> getValueFromReadables(K key,
          final KeyValuePairs<String, Object> requestParams) {
        Log.d("getValueFromReadables", key);
        return Observable.from(readableDataSources).flatMap(
              readableDataSource -> readableDataSource.getByKey(key, requestParams));
    }

    private Observable<List<V>> getValuesFromReadables(
          final KeyValuePairs<String, Object> requestPatams) {
        final List<V> resultList = new ArrayList<>();
        Log.d("getValuesFromReadables");
        return Observable.from(readableDataSources)
                         .flatMap(readableDataSource -> readableDataSource.getAll(requestPatams))
                         .collect(() -> resultList, (vs, vs2) -> vs.addAll(vs2));
    }

    private void populateCaches(V value) throws Exception {
        Log.d("populateCaches").v(value);
        getCacheDataSourceObservable()
              .flatMap(cacheDataSource -> cacheDataSource.addOrUpdateAsync(value)).subscribe();
    }

    private Observable<Boolean> removeFromCaches(final K key) {
        Log.d("removeFromCaches", key);
        return getCacheDataSourceObservable()
              .flatMap(cacheDataSource -> cacheDataSource.deleteByKeyAsync(key)).takeLast(1);
    }

    private Observable<Boolean> clearCaches() {
        Log.d("clearCaches");
        return getCacheDataSourceObservable()
              .flatMap(cacheDataSource -> cacheDataSource.deleteAllAsync()).takeLast(1);
    }

    @NonNull private Observable<AsyncCacheDataSource<K, V>> getCacheDataSourceObservable() {
        return Observable.from(cacheDataSources)
                         .toList().switchMap(dataSources -> {
                  int len = dataSources.size();
                  if (len > 0) {
                      switch (mCachePolicy) {
                          case CACHE_ONCE:
                              return Observable.just(dataSources.get(0));
                          case CACHE_NEVER:
                              return Observable.empty();
                          default:
                              return Observable.from(dataSources);
                      }
                  } else {
                      return Observable.empty();
                  }
              });
    }

    protected void populateCaches(Collection<V> values) throws Exception {
        getCacheDataSourceObservable().flatMap(
              cacheDataSource -> cacheDataSource.addOrUpdateAllAsync(values)).subscribe();
    }

    public Observable<Boolean> areCached(final List<V> values) {
        AtomicBoolean validValues = new AtomicBoolean(Boolean.FALSE);
        return getCacheDataSourceObservable()
              .flatMap(dataSource -> {
                  boolean allValid = true;
                  for (int i = 0, len = values.size(); i < len; i++) {
                      allValid &= dataSource.isValid(values.get(i));
                  }
                  return Observable.just(allValid);
              })
              .collect(() -> validValues, (result, valid) -> result.set(result.get() || valid))
              .flatMap(result -> Observable.just(result.get()));
    }

    public Observable<Boolean> isCached(V value) {
        AtomicBoolean validValues = new AtomicBoolean(Boolean.FALSE);
        return getCacheDataSourceObservable()
              .flatMap(dataSource -> Observable.just(dataSource.isValid(value)))
              .collect(() -> validValues, (result, valid) -> result.set(result.get() || valid))
              .flatMap(result -> Observable.just(result.get()));
    }

    private void validateKey(K key) {
        Preconditions.checkNotNull(key, "The key used can't be null.");
    }

    private void validateValue(V value) {
        Preconditions.checkNotNull(value, "The value used can't be null.");
    }

    private void validateValues(Collection<V> values) {
        Preconditions.checkNotNull(values, "The values used can't be null.");
    }
}
