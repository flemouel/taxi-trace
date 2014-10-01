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

package fr.insalyon.citi.trace.taxi;

/**
 * Gives a coordinate an Id to allow an easy comparison
 */
public class CoordinateId implements Comparable<CoordinateId> {

    private long taxiTimestamp;
    private int taxiNumber;

    public CoordinateId(int taxiNumber, long taxiTimestamp) {
        this.taxiNumber = taxiNumber;
        this.taxiTimestamp = taxiTimestamp;
    }

    /**
     * Gets the current date
     *
     * @return the timestamp of a taxi
     */
    public long getTaxiTimestamp() {
        return taxiTimestamp;
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
     * Checks if two coordinates are the same
     * true if taxi id and timestamp are the same
     *
     * @param o an object
     * @return a boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoordinateId that = (CoordinateId) o;

        if (taxiNumber != that.taxiNumber) return false;
        if (taxiTimestamp != that.taxiTimestamp) return false;

        return true;
    }

    /**
     * Orders two coordinates
     * first according to the time criterion
     * then according to the taxi id
     *
     * @param coordinateId of a taxi
     * @return int -1,0,1
     */
    public int compareTo(CoordinateId coordinateId) {
        if (taxiTimestamp < coordinateId.taxiTimestamp) return -1;
        if (taxiTimestamp > coordinateId.taxiTimestamp) return 1;
        if (taxiNumber < coordinateId.taxiNumber) return -1;
        if (taxiNumber > coordinateId.taxiNumber) return 1;
        return 0;
    }

    /**
     * Displays the coordinate description
     *
     * @return the string describing the taxi coordinate
     */
    @Override
    public String toString() {
        return "CoordinateId{" +
                "taxiTimestamp=" + taxiTimestamp +
                ", taxiNumber=" + taxiNumber +
                '}';
    }
}