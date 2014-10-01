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

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/**
 * Stores the taxi trace and generates the contact trace
 */
public class Trace extends RecursiveAction {

    public static boolean VERBOSE = true; // to display a process bar

    public static double DISTANCE_RANGE = 250; // meters
    public static long TIME_RANGE = 30; // seconds

    private TreeMap<CoordinateId, Coordinate> taxiCoordinates;
    private ConcurrentHashMap<Contact, Integer> taxiContacts;

    public Trace(TreeMap<CoordinateId, Coordinate> taxiCoordinates) {
        this.taxiCoordinates = taxiCoordinates;
        this.taxiContacts = null;
    }

    /**
     * Gets the contact trace (and generates it if needed)
     *
     * @return the contact trace
     */
    public ConcurrentHashMap<Contact, Integer> getTaxiContacts() {
        if (taxiContacts == null) generate();
        return taxiContacts;
    }

    /**
     * Saves the contact trace (and generates it if needed)
     *
     * @param file the destination file
     */
    public void dumpContactTrace(File file) {
        if (taxiContacts == null) generate();
        try {
            PrintStream out = new PrintStream(
                    new FileOutputStream(file));
            int nbIter = 0;
            for (Map.Entry<Contact, Integer> contact : taxiContacts.entrySet()) {
                if (VERBOSE && ((taxiContacts.entrySet().size() - nbIter) % (taxiContacts.entrySet().size() / 10) == 0))
                    System.out.print((taxiContacts.entrySet().size() - nbIter) * 10 / taxiContacts.entrySet().size() + "...");
                out.println(contact.getKey().toString());
                nbIter++;
            }
            out.close();
            if (VERBOSE) System.out.println("done.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Recursively reads the taxi trace from a directory
     *
     * @param dir the input directory
     * @return Trace the taxi trace read
     * @throws IOException
     * @throws ParseException
     */
    public static Trace fromDirectory(File dir) throws IOException, ParseException {
        TreeMap<CoordinateId, Coordinate> taxiCoordinates = new TreeMap<>();
        int nbIter = 0;
        for (File entry : dir.listFiles()) {
            if (VERBOSE && ((dir.listFiles().length - nbIter) % (dir.listFiles().length / 10) == 0))
                System.out.print((dir.listFiles().length - nbIter) * 10 / dir.listFiles().length + "...");
            if (entry.isDirectory()) {
                taxiCoordinates.putAll(fromDirectory(entry).taxiCoordinates);
            } else {
                taxiCoordinates.putAll(fromFile(entry).taxiCoordinates);
            }
            nbIter++;
        }
        if (VERBOSE) System.out.println("done.");
        return (new Trace(taxiCoordinates));
    }

    /**
     * Reads the taxi trace coordinates from a file
     *
     * The input lines from the file have to follow this format
     * (int) taxi number, (Date) yyyy-MM-dd HH:mm:ss, (long) longitude, (long) latitude, (int) speed, (int) direction degree, (int) status
     *
     * @param file the input file
     * @return Trace the taxi trace read
     * @throws IOException
     * @throws ParseException
     */
    public static Trace fromFile(File file) throws IOException, ParseException {
        TreeMap<CoordinateId, Coordinate> taxiCoordinates = new TreeMap<>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            while (line != null) {
                String[] tokens = line.split(",");
                int taxiNumber = Integer.parseInt(tokens[0].trim());
                Date taxiTimestamp = df.parse(tokens[1].trim());
                double taxiLongitude = Double.parseDouble(tokens[2].trim());
                double taxiLatitude = Double.parseDouble(tokens[3].trim());
                int taxiSpeed = Integer.parseInt(tokens[4].trim());
                int taxiDirection = Integer.parseInt(tokens[5].trim());
                int taxiStatus = Integer.parseInt(tokens[6].trim());
                Coordinate coordinate = new Coordinate(taxiNumber, taxiTimestamp, taxiLongitude, taxiLatitude, taxiSpeed, taxiDirection, taxiStatus);
                CoordinateId coordinateId = new CoordinateId(taxiNumber, taxiTimestamp.getTime());
                taxiCoordinates.put(coordinateId, coordinate);
                line = reader.readLine();
            }
        }
        return (new Trace(taxiCoordinates));
    }

    /**
     * Generates the contact trace
     */
    public void generate() {
        generateMultiThread();
        // generateSingleThread();
    }

    /**
     * Generates the contact trace with a mono-thread implementation
     */
    public void generateSingleThread() {
        taxiContacts = new ConcurrentHashMap<>();
        int nbIter = 0;
        for (Map.Entry<CoordinateId, Coordinate> entry : taxiCoordinates.entrySet()) {
            if (VERBOSE && ((taxiCoordinates.entrySet().size() - nbIter) % (taxiCoordinates.entrySet().size() / 10) == 0))
                System.out.print((taxiCoordinates.entrySet().size() - nbIter) * 10 / taxiCoordinates.entrySet().size() + "...");
            generateSubTrace(entry,
                    taxiCoordinates.subMap(entry.getKey(),
                            false,
                            new CoordinateId(999999, entry.getKey().getTaxiTimestamp() + TIME_RANGE * 1000),
                            true),
                    taxiContacts);
            nbIter++;
        }
        if (VERBOSE) System.out.println("done.");
    }

    /**
     * Computes the GPS distance between two taxi coordinates to determine if a contact exists
     *
     * @param entry the first GPS coordinate to examine
     * @param subMap the following GPS coordinates (in time order) to compare to
     * @param taxiContacts to generated contact trace where to add potential contacts
     */
    public void generateSubTrace(Map.Entry<CoordinateId, Coordinate> entry, Map<CoordinateId, Coordinate> subMap, ConcurrentHashMap<Contact, Integer> taxiContacts) {
        for (Map.Entry<CoordinateId, Coordinate> follower : subMap.entrySet()) {
            if (entry.getValue().getTaxiNumber() != follower.getValue().getTaxiNumber()
                    && entry.getValue().distance(follower.getValue()) <= DISTANCE_RANGE) {
                taxiContacts.put(new Contact(entry.getValue().getTaxiNumber(),
                        follower.getValue().getTaxiNumber(),
                        entry.getValue().getTaxiTimestamp().getTime(),
                        follower.getValue().getTaxiTimestamp().getTime()), new Integer(0));
            }
        }
    }

    /**
     * Generates the contact trace with a multi-thread implementation
     */
    public void generateMultiThread() {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(this);
    }

    /**
     * Overrides compute method from RecursiveAction
     * Splits the taxi trace analysis through different SubContactTraceGeneratorTask in different threads
     */
    @Override
    protected void compute() {
        taxiContacts = new ConcurrentHashMap<>();
        LinkedList<RecursiveAction> forks = new LinkedList<>();
        int nbIter = 0;
        for (Map.Entry<CoordinateId, Coordinate> entry : taxiCoordinates.entrySet()) {
            if (VERBOSE && ((taxiCoordinates.entrySet().size() - nbIter) % (taxiCoordinates.entrySet().size() / 10) == 0))
                System.out.print((taxiCoordinates.entrySet().size() - nbIter) * 10 / taxiCoordinates.entrySet().size() + "...");
            SubContactTraceGeneratorTask subTask = new SubContactTraceGeneratorTask(entry,
                    taxiCoordinates.subMap(entry.getKey(),
                            false,
                            new CoordinateId(999999, entry.getKey().getTaxiTimestamp() + TIME_RANGE * 1000),
                            true),
                    taxiContacts
            );
            forks.add(subTask);
            subTask.fork();
            nbIter++;
        }
        if (VERBOSE) System.out.println("threads launched.");
        nbIter = 0;
        for (RecursiveAction task : forks) {
            if (VERBOSE && ((forks.size() - nbIter) % (forks.size() / 10) == 0))
                System.out.print((forks.size() - nbIter) * 10 / forks.size() + "...");
            task.join();
            nbIter++;
        }
        if (VERBOSE) System.out.println("done.");
    }

    /**
     * Displays only the length of the original taxi trace (i.e. the coordinates number)
     * and the length of the generated contact trace (i.e. the detected contact number)
     *
     * @return the string displaying the lengths
     */
    @Override
    public String toString() {
        int contactLength;
        if (taxiContacts == null) {
            contactLength = 0;
        } else {
            contactLength = taxiContacts.size();
        }
        return "Trace{" +
                "traceLength=" + taxiCoordinates.size() +
                ", contactLength=" + contactLength +
                '}';
    }
}