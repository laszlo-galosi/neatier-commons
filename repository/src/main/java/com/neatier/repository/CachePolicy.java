package com.neatier.repository;

/**
 * Enum class for different cache policy.
 *
 * @author László Gálosi
 * @since 12/06/16
 */
public enum CachePolicy {
    /**
     * Never cache the entry.
     */
    CACHE_NEVER,

    /**
     * Cache only once.
     */
    CACHE_ONCE,

    /**
     * Cache multiple times.
     */
    CACHE_ALL;

    public boolean writeCache() {
        return this == CACHE_ONCE || this == CACHE_ALL;
    }
}
