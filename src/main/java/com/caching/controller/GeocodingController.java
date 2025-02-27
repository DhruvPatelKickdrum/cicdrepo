package com.caching.controller;

import com.caching.service.GeocodingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GeocodingController {

    private final GeocodingService geocodingService;


    /**
     * Handles HTTP GET requests for forward geocoding to convert an address into geographic coordinates.
     *
     * @param address The address to be converted into latitude and longitude.
     * @return A ResponseEntity containing a Map with latitude and longitude if the address is valid,
     * HttpStatus.BAD_REQUEST if the address is null or blank, or HttpStatus.NOT_FOUND if no coordinates are found.
     */
    @GetMapping("/geocoding")
    public ResponseEntity<Map<String, Double>> forwardGeocoding(@RequestParam("address") String address) {
        log.debug("Forward geocoding request received for address: {}", address);
        //validating the address
        if (address == null || address.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Handle empty or null input
        }
        Map<String, Double> coordinates = geocodingService.getCoordinates(address);
        //validating the coordinates
        if (coordinates == null || coordinates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Handle invalid address
        }
        return ResponseEntity.ok(coordinates);
    }
    @GetMapping("/")
    public ResponseEntity<String> home() {

        return ResponseEntity.ok("Welcome to EBS Caching Application -Dhruv");
    }

    /**
     * Handles HTTP GET requests for reverse geocoding to convert geographic coordinates into an address.
     *
     * @param latitude  The latitude of the coordinates to be converted into an address.
     * @param longitude The longitude of the coordinates to be converted into an address.
     * @return A ResponseEntity containing the resolved address if the coordinates are valid, or
     * ResponseEntity.status(HttpStatus.BAD_REQUEST) if the coordinates are invalid, or
     * ResponseEntity.status(HttpStatus.NOT_FOUND) if no address is found for the coordinates.
     */
    @GetMapping("/reverse-geocoding")
    public ResponseEntity<String> reverseGeocoding(@RequestParam("latitude") Double latitude,
                                                   @RequestParam("longitude") Double longitude) {
        log.debug("Reverse geocoding request received for coordinates: {}, {}", latitude, longitude);

        // Validate latitude and longitude values
        if (latitude == null || longitude == null || latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid latitude or longitude. Latitude must be between -90 and 90, and longitude between -180 and 180.");
        }

        // Call the service to get the address
        String address = geocodingService.getAddress(latitude, longitude);

        // Check if the service returned a valid address
        if (address == null || address.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No address found for the provided coordinates.");
        }

        // Return the resolved address
        return ResponseEntity.ok(address);
    }


    /**
     * Handles HTTP DELETE requests to evict all cache entries from the 'geocoding' cache.
     * This endpoint is useful for testing and debugging purposes, as it allows the cache to be cleared.
     */
    @DeleteMapping("/evict-geocoding-cache")
    public void evictAllGeocodingCache() {
        log.debug("Evicting all entries from 'geocoding' cache.");
        geocodingService.evictAllGeocodingCache();
    }

    /**
     * Handles HTTP DELETE requests to evict all cache entries from the 'reverse-geocoding' cache.
     * This endpoint is useful for testing and debugging purposes, as it allows the cache to be cleared.
     */
    @DeleteMapping("/evict-reverse-geocoding-cache")
    public void evictAllReverseGeocodingCache() {
        log.debug("Evicting all entries from 'reverse-geocoding' cache.");
        geocodingService.evictAllReverseGeocodingCache();
    }

    /**
     * Handles HTTP DELETE requests to evict a specific cache entry from the 'geocoding' cache.
     * This endpoint is useful for removing cached geocoding data for a specific address.
     *
     * @param address The address whose cache entry should be evicted.
     */
    @DeleteMapping("/evict-geocoding-entry")
    public void evictSpecificGeocodingEntry(@RequestParam("address") String address) {
        log.debug("Evicting geocoding cache entry for address: {}", address);
        geocodingService.evictSpecificGeocodingEntry(address);
    }

    /**
     * Handles HTTP DELETE requests to evict a specific cache entry from the 'reverse-geocoding' cache.
     * This endpoint is useful for removing cached reverse geocoding data for a specific set of coordinates.
     *
     * @param latitude  The latitude value of the coordinates whose cache entry should be evicted.
     * @param longitude The longitude value of the coordinates whose cache entry should be evicted.
     */
    @DeleteMapping("/evict-reverse-geocoding-entry")
    public void evictSpecificReverseGeocodingEntry(@RequestParam("latitude") double latitude,
                                                   @RequestParam("longitude") double longitude) {
        log.debug("Evicting reverse-geocoding cache entry for coordinates: {}, {}", latitude, longitude);
        geocodingService.evictSpecificReverseGeocodingEntry(latitude, longitude);
    }

    /**
     * Handles HTTP DELETE requests to evict stale cache entries from the 'geocoding' cache.
     * This endpoint is useful for maintaining cache efficiency by removing outdated geocoding data.
     */
    @DeleteMapping("/evict-stale-geocoding-entries")
    public void evictStaleGeocodingEntries() {
        log.debug("Evicting stale entries from 'geocoding' cache.");
        geocodingService.evictStaleGeocodingEntries();
    }

    /**
     * Handles HTTP DELETE requests to evict stale cache entries from the 'reverse-geocoding' cache.
     * This endpoint is useful for maintaining cache efficiency by removing outdated reverse geocoding data.
     */
    @DeleteMapping("/evict-stale-reverse-geocoding-entries")
    public void evictStaleReverseGeocodingEntries() {
        log.debug("Evicting stale entries from 'reverse-geocoding' cache.");
        geocodingService.evictStaleReverseGeocodingEntries();
    }
}
