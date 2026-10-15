package com.bloodmatcher.matching;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DistanceCalculatorServiceTest {

    private DistanceCalculatorService distanceCalculator;

    @BeforeEach
    void setUp() {
        distanceCalculator = new DistanceCalculatorService();
    }

    @Test
    @DisplayName("Haversine calculates accurate distance between London and Paris")
    void testLondonToParis() {
        // London: 51.5074 N, 0.1278 W
        // Paris: 48.8566 N, 2.3522 E
        Double distance = distanceCalculator.calculateDistanceKm(51.5074, -0.1278, 48.8566, 2.3522);
        assertNotNull(distance);
        // Distance is ~343.5 km
        assertTrue(distance > 340.0 && distance < 350.0, "Expected ~343.5 km but got: " + distance);
    }

    @Test
    @DisplayName("Distance to same coordinates is 0.0 km")
    void testSameCoordinates() {
        Double distance = distanceCalculator.calculateDistanceKm(40.7128, -74.0060, 40.7128, -74.0060);
        assertNotNull(distance);
        assertEquals(0.0, distance);
    }

    @Test
    @DisplayName("Missing coordinates return null distance instead of guessing")
    void testMissingCoordinatesReturnNull() {
        assertNull(distanceCalculator.calculateDistanceKm(null, -74.0060, 40.7128, -74.0060));
        assertNull(distanceCalculator.calculateDistanceKm(40.7128, null, 40.7128, -74.0060));
        assertNull(distanceCalculator.calculateDistanceKm(40.7128, -74.0060, null, -74.0060));
        assertNull(distanceCalculator.calculateDistanceKm(40.7128, -74.0060, 40.7128, null));
    }

    @Test
    @DisplayName("Invalid latitude (>90 or <-90) throws IllegalArgumentException")
    void testInvalidLatitudeThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                distanceCalculator.calculateDistanceKm(91.0, 0.0, 10.0, 10.0));
        assertThrows(IllegalArgumentException.class, () ->
                distanceCalculator.calculateDistanceKm(-95.0, 0.0, 10.0, 10.0));
        assertThrows(IllegalArgumentException.class, () ->
                distanceCalculator.calculateDistanceKm(10.0, 0.0, 95.0, 10.0));
    }

    @Test
    @DisplayName("Invalid longitude (>180 or <-180) throws IllegalArgumentException")
    void testInvalidLongitudeThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                distanceCalculator.calculateDistanceKm(10.0, 185.0, 10.0, 10.0));
        assertThrows(IllegalArgumentException.class, () ->
                distanceCalculator.calculateDistanceKm(10.0, -181.0, 10.0, 10.0));
        assertThrows(IllegalArgumentException.class, () ->
                distanceCalculator.calculateDistanceKm(10.0, 0.0, 10.0, 185.0));
    }
}
