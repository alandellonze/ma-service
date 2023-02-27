package it.ade.ma.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
public class CacheConfiguration {

    public static final String CACHE_NAME = "webPageContent";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CACHE_NAME);
    }

    @Scheduled(fixedRateString = "${ma.metal-archives.cacheTTL}")
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void emptyCache() {
        log.info("emptying cache");
    }

}
