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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveAction;

/**
 * Includes the task code to execute in each thread to analyze a sub-part of the taxi trace
 */
public class SubContactTraceGeneratorTask extends RecursiveAction {

    private Map.Entry<CoordinateId, Coordinate> entry;
    private Map<CoordinateId, Coordinate> subMap;
    private ConcurrentHashMap<Contact, Integer> taxiContacts;

    public SubContactTraceGeneratorTask(Map.Entry<CoordinateId, Coordinate> entry, Map<CoordinateId, Coordinate> subMap, ConcurrentHashMap<Contact, Integer> taxiContacts) {
        this.entry = entry;
        this.subMap = subMap;
        this.taxiContacts = taxiContacts;
    }

    /**
     * Computes the GPS distance between two taxi coordinates to determine if a contact exists
     *
     * Parameters are similar to generateSubTrace from Trace class, except their are given
     * through the class constructor
     */
    @Override
    protected void compute() {
        for (Map.Entry<CoordinateId, Coordinate> follower : subMap.entrySet()) {
            if (entry.getValue().getTaxiNumber() != follower.getValue().getTaxiNumber()
                    && entry.getValue().distance(follower.getValue()) <= Trace.DISTANCE_RANGE) {
                taxiContacts.put(new Contact(entry.getValue().getTaxiNumber(),
                        follower.getValue().getTaxiNumber(),
                        entry.getValue().getTaxiTimestamp().getTime(),
                        follower.getValue().getTaxiTimestamp().getTime()), new Integer(0));
            }
        }
    }
}
