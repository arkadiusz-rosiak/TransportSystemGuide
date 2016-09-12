package pl.rosiakit.utils;

/**
 * @author https://rosettacode.org/wiki/Haversine_formula#Java
 * @date 2016-09-02
 */
public class Haversine {
    private static final double R = 6372800; // Earth radius in meters

    /**
     * This method calculates distance between 2 given points.
     * @param lat1 latitude of the first point
     * @param lon1 longitude of the first point
     * @param lat2 latitude of the second point
     * @param lon2 longitude of the second points
     * @return distance in METERS
     */
    public static double calculateDistanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}