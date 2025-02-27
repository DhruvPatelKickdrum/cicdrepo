# Geocoding and Reverse Geocoding API using SpringBoot-CI/CD

This repository contains a SpringBoot application that provides a REST API for forward and reverse geocoding. The API uses the Positionstack API for geocoding and reverse geocoding. The application also uses in-memory caching to store the geocoding and reverse geocoding results.

The application provides the following endpoints:

* Forward Geocoding: `http://localhost:${port}/geocoding?address=${value}`
* Reverse Geocoding: `http://localhost:${port}/reverse-geocoding?latitude=${value}&longitude=${value}`

The application uses the following caching keys and names:

* Forward Geocoding key: `#address`
* Forward Geocoding name: `geocoding`
* Reverse Geocoding key: `#latitude,#longitude`
* Reverse Geocoding name: `reverse-geocoding`

The application has the following requirements:

* The application must have in-memory caching support
* The application must lookup in an in-memory cache for lat/long corresponding to an address and address corresponding to latitude and longitude
* If the record is not found in the cache, the application must invoke the third party location API to fetch the lat/long for a given address and persist that in the in-memory cache
* The application must remove one or more/all stale and unused records from cache so that fresh values can be loaded into the cache again
* The application must avoid evicting too much data out of the cache by selectively updating the entries whenever you alter them
* The application must clearly log the mapping for lat/long corresponding to an address and address corresponding to latitude and longitude

The application has the following test cases:

* `testGetGeoCode`: Test case to verify the successful retrieval of geocoding information from the external API via the `/geocoding` endpoint
* `testGetReverseGeoCode`: Test case to verify the successful retrieval of reverse geocoding information from the external API via the "/reverse-geocoding" endpoint
* `testGeoCodingCacheHitWithEndpoint`: Test case to verify that the cache is populated and successfully retrieved on the second call for the geocoding endpoint
* `testReverseGeoCodingCacheHitWithEndpoint`: Test case to verify that the cache is populated and successfully retrieved on the second call for the reverse geocoding endpoint
* `testGeoCodingCacheMiss`: Test case to verify that calling the geocoding endpoint with a specific address- *goa* results in a cache miss
* `testGeoCodingCacheEviction`: Test case to verify the eviction of cache entries for geocoding information
* `testGetGeoCodeNegative`: Test case to verify the geocoding endpoint with an invalid address
* `testGetReverseGeoCodeNegative`: Test case to verify the reverse geocoding endpoint with invalid parameters

The application also contains a drift video that explains the implementation.