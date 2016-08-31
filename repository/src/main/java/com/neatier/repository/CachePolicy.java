package com.neatier.repository;

/**
 * Created by László Gálosi on 12/06/16
 */
public enum CachePolicy {
    CACHE_NEVER,
    CACHE_ONCE,
    CACHE_ALL;

    public boolean writeCache() {
        return this == CACHE_ONCE || this == CACHE_ALL;
    }
}
