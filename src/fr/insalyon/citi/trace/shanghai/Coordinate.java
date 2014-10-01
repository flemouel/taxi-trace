/*
 * Copyright 2013-2014 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Frédéric Le Mouël
 */

package fr.insalyon.citi.trace.shanghai;

import java.util.Date;

/**
 * Represents the description of a taxi (location, speed, direction, status) at a given time.
 */
public class Coordinate {

    private int taxiNumber;
    private Date taxiTimestamp;
    private double taxiLongitude;
    private double taxiLatitude;
    private int taxiSpeed;
    private int taxiDirection;
    private int taxiStatus;

    public Coordinate(int taxiNumber,
                      Date taxiTimestamp,
                      double taxiLongitude,
                      double taxiLatitude,
                      int taxiSpeed,
                      int taxiDirection,
                      int taxiStatus) {
        this.taxiNumber = taxiNumber;
        this.taxiTimestamp = taxiTimestamp;
        this.taxiLongitude = taxiLongitude;
        this.taxiLatitude = taxiLatitude;
        this.taxiSpeed = taxiSpeed;
        this.taxiDirection = taxiDirection;
        this.taxiStatus = taxiStatus;
    }

    /**
     * Identifies a taxi
     *
     * @return the id of a taxi
     */
    public int getTaxiNumber() {
        return taxiNumber;
    }

    /**
     * Gets the current date
     *
     * @return the timestamp of a taxi
     */
    public Date getTaxiTimestamp() {
        return taxiTimestamp;
    }

    /**
     * Computes the distance between two taxi coordinates:
     * the current and the given one
     *
     * @param coordinate of a taxi
     * @return the distance in meters
     */
    public double distance(Coordinate coordinate) {
        return distanceHaversine(coordinate);
    }

    /**
     * Computes the distance between two taxi coordinates
     * using the perfect circle earth metric between the two GPS coordinates
     *
     * Source: stackoverflow
     * http://stackoverflow.com/questions/120283/how-can-i-measure-distance-and-create-a-bounding-box-based-on-two-latitudelongi
     *
     * Accuracy: -, Efficiency: ~, Order magnitude: 1
     *
     * @param coordinate of a taxi
     * @return the distance in meters
     */
    public double distanceGeometric(Coordinate coordinate) {
        double theta = taxiLongitude - coordinate.taxiLongitude;
        double dist = Math.sin(deg2rad(taxiLatitude)) * Math.sin(deg2rad(coordinate.taxiLatitude))
                + Math.cos(deg2rad(taxiLatitude)) * Math.cos(deg2rad(coordinate.taxiLatitude)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515; // miles
        dist = dist * 1.609344; // kilometers
        dist = dist * 1000; // meters
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    /**
     * Computes the distance between two taxi coordinates
     * using the Haversine formulae between the two GPS coordinates
     *
     * Source: stackoverflow
     * http://stackoverflow.com/questions/120283/how-can-i-measure-distance-and-create-a-bounding-box-based-on-two-latitudelongi
     *
     * Accuracy: +, Efficiency: +, Order magnitude: 0.5
     *
     * @param coordinate of a taxi
     * @return the distance in meters
     */
    public double distanceHaversine(Coordinate coordinate) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(coordinate.taxiLatitude - taxiLatitude);
        double dLng = Math.toRadians(coordinate.taxiLongitude - taxiLongitude);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(taxiLatitude)) * Math.cos(Math.toRadians(coordinate.taxiLatitude));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        dist = dist * 1.609344; // kilometers
        dist = dist * 1000; // meters
        return (dist);
    }

    /**
     * Computes the distance between two taxi coordinates
     * using the Vincenty formulae between the two GPS coordinates
     *
     * Source: stackoverflow
     * http://stackoverflow.com/questions/120283/how-can-i-measure-distance-and-create-a-bounding-box-based-on-two-latitudelongi
     *
     * Accuracy: ++, Efficiency: --, Order magnitude: 2
     *
     * @param coordinate of a taxi
     * @return the distance in meters
     */
    public double distanceVincenty(Coordinate coordinate) {
        double a = 6378137, b = 6356752.314245, f = 1 / 298.257223563; // WGS-84 ellipsoid params
        double L = Math.toRadians(coordinate.taxiLongitude - taxiLongitude);
        double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(taxiLatitude)));
        double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(coordinate.taxiLatitude)));
        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

        double sinLambda, cosLambda, sinSigma, cosSigma, sigma, sinAlpha, cosSqAlpha, cos2SigmaM;
        double lambda = L, lambdaP, iterLimit = 100;
        do {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda)
                    + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
            if (sinSigma == 0)
                return 0; // co-incident points
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            if (Double.isNaN(cos2SigmaM))
                cos2SigmaM = 0; // equatorial line: cosSqAlpha=0 (§6)
            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = L + (1 - C) * f * sinAlpha
                    * (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0)
            return Double.NaN; // formula failed to converge

        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma = B
                * sinSigma
                * (cos2SigmaM + B
                / 4
                * (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
                * (-3 + 4 * sinSigma * sinSigma) * (-3 + 4 * cos2SigmaM * cos2SigmaM)));
        double dist = b * A * (sigma - deltaSigma);

        return dist;
    }

    /**
     * Displays the coordinate description
     *
     * @return the string describing the taxi coordinate
     */
    @Override
    public String toString() {
        return "Coordinate{" +
                "taxiNumber=" + taxiNumber +
                ", taxiTimestamp=" + taxiTimestamp +
                ", taxiLongitude=" + taxiLongitude +
                ", taxiLatitude=" + taxiLatitude +
                ", taxiSpeed=" + taxiSpeed +
                ", taxiDirection=" + taxiDirection +
                ", taxiStatus=" + taxiStatus +
                '}';
    }
}