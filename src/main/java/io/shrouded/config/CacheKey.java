package io.shrouded.config;

public final class CacheKey {

    private CacheKey() {
        // prevent instantiation
    }

    public static final String SQL_QUERY_CACHE = "sql-query-cache";
    public static final String GRID_SPATIAL_INDEX_CACHE = "grid-spatial-index-cache";
    public static final String VOLATILE_CACHE = "volatile-cache";

}
