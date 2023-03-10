package com.jwj.community.config.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@EnableCaching
@Configuration
public class CachingConfig {

    @Bean
    public CacheManager cacheManager(){
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        List<Cache> authCacheStore = Arrays.asList(new ConcurrentMapCache("roleResourcesCacheStore"));
        simpleCacheManager.setCaches(authCacheStore);
        return simpleCacheManager;
    }
}
