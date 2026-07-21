package com.triage.dera.utility;

public class HaversineMathUtility {

    private static final double EARTH_RADIUS_KM = 6371.0;
    public static Double calcGeoDistance (Double primLat, Double primLong, Double newLat, Double newLong){
        double latDiff = Math.toRadians(newLat - primLat);
        double longDiff = Math.toRadians(newLong - primLong);

        double calc = Math.sin(latDiff / 2) * Math.sin(latDiff / 2)
                + Math.cos(Math.toRadians(primLat)) * Math.cos(Math.toRadians(newLat))
                * Math.sin(longDiff / 2) * Math.sin(longDiff / 2);

        double calc2 = 2 * Math.atan2(Math.sqrt(calc), Math.sqrt(1 - calc));

        return EARTH_RADIUS_KM * calc2;
    }
}
