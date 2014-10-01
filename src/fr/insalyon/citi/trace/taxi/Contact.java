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

import java.io.Serializable;

/**
 * Represents a potential communication contact between two taxis
 */
public class Contact implements Comparable<Contact>, Serializable {

    private int taxi1;
    private int taxi2;
    private long start;
    private long stop;

    public Contact(int taxi1, int taxi2, long start, long stop) {
        this.taxi1 = taxi1;
        this.taxi2 = taxi2;
        this.start = start;
        this.stop = stop;
    }

    /**
     * Checks if two contacts are the same
     * true if both taxis and duration are the same
     *
     * @param o an object
     * @return a boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (taxi1 != contact.taxi1) return false;
        if (taxi2 != contact.taxi2) return false;
        if (start != contact.start) return false;
        if (stop != contact.stop) return false;

        return true;
    }

    /**
     * Orders two contacts
     * first according to the first taxi id
     * then according to the second taxi id
     * then according to the contact starting time
     * then according to the contact ending time
     *
     * @param contact between two taxis
     * @return int -1,0,1
     */
    public int compareTo(Contact contact) {
        if (taxi1 < contact.taxi1) return -1;
        if (taxi1 > contact.taxi1) return 1;
        if (taxi2 < contact.taxi2) return -1;
        if (taxi2 > contact.taxi2) return 1;
        if (start < contact.start) return -1;
        if (start > contact.start) return 1;
        if (stop < contact.stop) return -1;
        if (stop > contact.stop) return 1;
        return 0;
    }

    /**
     * Displays the contact description
     *
     * @return the string describing the taxis contact
     */
    @Override
    public String toString() {
        return taxi1 +
                " " + taxi2 +
                " " + start +
                " " + stop;
    }
}