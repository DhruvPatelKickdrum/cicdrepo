package com.caching.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {


    /**
     * Create a cache manager using the caffeine cache builder.
     *
     * @return a cache manager using the caffeine cache builder
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    /**
     * Builds a Caffeine cache builder to be used by the cache manager.
     * The cache builder is configured to expire cache entries after 1 hour and to hold a maximum of 1000 entries.
     *
     * @return a {@link Caffeine} cache builder
     */
    @Bean
    public Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS) // Cache entries expire after 1 hour
                .maximumSize(1000); // Maximum number of entries in the cache
    }
}
