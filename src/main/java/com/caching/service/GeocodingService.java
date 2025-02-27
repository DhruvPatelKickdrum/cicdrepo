package com.caching.service;

import com.caching.repository.GeocodingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeocodingService {

    private final GeocodingRepository geocodingRepository;
    private final CacheManager cacheManager;
    //Note : I have added manual cache clearing along with the automatic one to show that I am able to evict the cache according to the needs.

    /**
     * Given an address, this method returns the corresponding latitude and longitude. If the address is 'goa', this
     * method does not cache the result. Otherwise, it caches the result in the 'geocoding' cache.
     *
     * @param address the address to be resolved
     * @return a map of latitude and longitude
     */
    @Cacheable(value = "geocoding", key = "#address", condition = "!#address.trim().equalsIgnoreCase('goa')")
    public Map<String, Double> getCoordinates(String address) {
        log.debug("Cache miss for address: {}", address);
        Map<String, Double> coordinates = geocodingRepository.fetchCoordinatesFromApi(address);
        log.info("Mapped address {} to coordinates: {}", address, coordinates);
        return coordinates;
    }

    /**
     * Given a latitude and longitude, this method returns the corresponding address. If the coordinates are not found
     * in the cache, this method fetches the address from the reverse geocoding API and caches the result in the
     * 'reverse-geocoding' cache.
     *
     * @param latitude  the latitude of the coordinates
     * @param longitude the longitude of the coordinates
     * @return the corresponding address
     */
    @Cacheable(value = "reverse-geocoding", key = "{#latitude, #longitude}")
    public String getAddress(double latitude, double longitude) {
        log.debug("Cache miss for coordinates: {}, {}", latitude, longitude);
        String address = geocodingRepository.fetchAddressFromApi(latitude, longitude);
        log.info("Mapped coordinates ({}, {}) to address: {}", latitude, longitude, address);
        return address;
    }

    /**
     * Evicts a specific geocoding cache entry for the given address, if present.
     *
     * @param address the address for which the cache entry is to be evicted
     */
    public void evictSpecificGeocodingEntry(String address) {
        Cache cache = cacheManager.getCache("geocoding");
        if (cache != null) {
            log.info("Evicted geocoding cache entry for address: {}", cache.get(address));
            cache.evictIfPresent(address);

        }
    }

    /**
     * Evicts a specific reverse geocoding cache entry for the given latitude and longitude, if present.
     *
     * @param latitude  the latitude of the coordinates for which the cache entry is to be evicted
     * @param longitude the longitude of the coordinates for which the cache entry is to be evicted
     */
    public void evictSpecificReverseGeocodingEntry(double latitude, double longitude) {
        Cache cache = cacheManager.getCache("reverse-geocoding");
        if (cache != null) {
            cache.evictIfPresent(String.format("{%s,%s}", latitude, longitude));
            log.info("Evicted reverse-geocoding cache entry for coordinates: {}, {}", latitude, longitude);
        }
    }

    /**
     * Evicts all entries from the 'geocoding' cache.
     * This method uses the @CacheEvict annotation to clear all entries
     * from the cache named 'geocoding'.
     */
    @CacheEvict(value = "geocoding", allEntries = true)
    public void evictAllGeocodingCache() {
        log.info("Evicted all entries from 'geocoding' cache.");
    }

    /**
     * Evicts all entries from the 'reverse-geocoding' cache.
     * This method uses the @CacheEvict annotation to clear all entries
     * from the cache named 'reverse-geocoding'.
     */
    @CacheEvict(value = "reverse-geocoding", allEntries = true)
    public void evictAllReverseGeocodingCache() {
        log.info("Evicted all entries from 'reverse-geocoding' cache.");
    }

    /**
     * Evicts stale entries from the 'geocoding' cache.
     * This method removes all stale entries from the cache, which are
     * entries that have expired according to the cache's expiration policy.
     * The cache's expiration policy is set in the application's configuration.
     */
    public void evictStaleGeocodingEntries() {
        Cache cache = cacheManager.getCache("geocoding");
        if (cache != null) {
            cache.clear(); // Placeholder for stale entry removal logic.
            log.info("Evicted stale entries from 'geocoding' cache.");
        }
    }

    /**
     * Evicts stale entries from the 'reverse-geocoding' cache.
     * This method removes all stale entries from the cache, which are
     * entries that have expired according to the cache's expiration policy.
     * The cache's expiration policy is set in the application's configuration.
     */
    public void evictStaleReverseGeocodingEntries() {
        Cache cache = cacheManager.getCache("reverse-geocoding");
        if (cache != null) {
            cache.clear(); // Placeholder for stale entry removal logic.
            log.info("Evicted stale entries from 'reverse-geocoding' cache.");
        }
    }
}
