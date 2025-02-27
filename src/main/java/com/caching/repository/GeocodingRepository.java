package com.caching.repository;

import com.caching.exceptions.DataFetchingFailException;
import com.caching.exceptions.InvalidAddressException;
import com.caching.exceptions.InvalidCoordinateException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@PropertySource("classpath:application.properties")
public class GeocodingRepository {

    //For passing the test cases and running in main I am not able to use two application.properties files hence I am using this
    private static final String GEOCODINGURL = "http://api.positionstack.com/v1/forward?access_key=878e863a37d2cd347650faa1a7e60d51&query=";
    private static final String REVERSEGEOCODINGURL = "http://api.positionstack.com/v1/reverse?access_key=878e863a37d2cd347650faa1a7e60d51&query=";
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Fetches the coordinates from the geocoding API for the given address.
     *
     * @param address the address to fetch coordinates for
     * @return a map containing the "latitude" and "longitude" of the given address
     * @throws DataFetchingFailException if the API request fails
     * @throws InvalidAddressException   if the given address is invalid
     */
    public Map<String, Double> fetchCoordinatesFromApi(String address) {
        //Creating the url as per the format
        String url = String.format("%s%s", GEOCODINGURL, address);
        //Calling the geocoding API
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                // Convert JSON string to Map
                Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
                // Get the first result
                List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");
                if (data != null && !data.isEmpty()) {
                    // Get the latitude and longitude
                    Map<String, Object> firstResult = data.get(0);
                    Double latitude = (Double) firstResult.get("latitude");
                    Double longitude = (Double) firstResult.get("longitude");
                    // Return the coordinates
                    Map<String, Double> coordinates = new HashMap<>();
                    coordinates.put("latitude", latitude);
                    coordinates.put("longitude", longitude);
                    return coordinates;
                } else {
                    // Throwing error to Handle invalid address
                    throw new InvalidAddressException("No data found for the given address.");
                }
            } catch (JsonProcessingException e) {
                // Handle JSON parsing error
                log.error("Error parsing geocoding API response: {}", e.getMessage());
            }
        } else {
            // Handle other HTTP status codes
            throw new DataFetchingFailException("Failed to fetch geocoding data. HTTP status: " + response.getStatusCode());
        }
        return Collections.emptyMap();
    }

    /**
     * Fetches the address from the reverse geocoding API for the given latitude and longitude.
     *
     * @param latitude  the latitude of the coordinates
     * @param longitude the longitude of the coordinates
     * @return the address for the given coordinates
     * @throws DataFetchingFailException  if the API request fails
     * @throws InvalidCoordinateException if the given latitude and longitude are invalid
     */
    public String fetchAddressFromApi(double latitude, double longitude) {
        //Creating the url as per the format
        String url = String.format("%s%s,%s", REVERSEGEOCODINGURL, latitude, longitude);
        //Calling the reverse geocoding API
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        //Handling the response
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                // Convert JSON string to Map
                Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
                // Get the first result
                List<Map<String, Object>> data = Collections.unmodifiableList((List<Map<String, Object>>) responseBody.get("data"));
                if (!data.isEmpty()) {
                    Map<String, Object> firstResult = data.get(0);
                    return (String) firstResult.get("label");
                } else {
                    // Throwing error to Handle invalid coordinates
                    throw new InvalidCoordinateException("No data found for the given coordinates.");
                }
            } catch (JsonProcessingException e) {
                // Handle JSON parsing error
                log.error("Error parsing reverse geocoding API response: {}", e.getMessage());
            }
        } else {
            // Handle other HTTP status codes
            throw new DataFetchingFailException("Failed to fetch reverse geocoding data. HTTP status: " + response.getStatusCode());
        }
        return "";
    }

}
