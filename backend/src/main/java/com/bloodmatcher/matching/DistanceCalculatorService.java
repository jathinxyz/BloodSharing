package com.bloodmatcher.matching;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service to calculate spherical surface distance between geographical coordinates
 * using the Haversine formula.
 */
@Service
public class DistanceCalculatorService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculates the great-circle distance between two points in kilometers.
     * Returns null if any coordinate is null.
     *
     * @throws IllegalArgumentException if any non-null coordinate is out of bounds
     */
    public Double calculateDistanceKm(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return null;
        }

        validateCoordinates(lat1, lon1, "Point 1");
        validateCoordinates(lat2, lon2, "Point 2");

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double rLat1 = Math.toRadians(lat1);
        double rLat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(rLat1) * Math.cos(rLat2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS_KM * c;

        return BigDecimal.valueOf(distance)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public boolean isValidCoordinate(Double lat, Double lon) {
        if (lat == null || lon == null) {
            return false;
        }
        return lat >= -90.0 && lat <= 90.0 && lon >= -180.0 && lon <= 180.0;
    }

    public void validateCoordinates(Double lat, Double lon, String label) {
        if (lat < -90.0 || lat > 90.0) {
            throw new IllegalArgumentException(label + " latitude " + lat + " is out of range [-90, 90]");
        }
        if (lon < -180.0 || lon > 180.0) {
            throw new IllegalArgumentException(label + " longitude " + lon + " is out of range [-180, 180]");
        }
    }
}
