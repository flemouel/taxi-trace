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

import java.io.File;

/**
 * Launches the contact trace generator
 */
public class LaunchContactTraceGenerator {

    public static void main(String args[]) throws Exception {
        if (args.length != 2) {
            System.out.println("usage: java --classpath <your-class-dir> fr.insalyon.citi.trace.taxi.LaunchContactTraceGenerator <taxi-trace-source-dir> <contact-trace-dest-file>");
            return;
        }
        System.out.println("----------------------");
        System.out.println("Mobility Trace loading");
        System.out.println("----------------------");
        long start = System.currentTimeMillis();
        Trace trace = Trace.fromDirectory(new File(args[0]));
        long stop = System.currentTimeMillis();
        System.out.println("Trace - loading: " + (stop - start) + "ms");
        System.out.println("-------------");
        System.out.println("Trace display");
        System.out.println("-------------");
        System.out.println("Trace - current: " + trace);
        System.out.println("------------------------");
        System.out.println("Contact Trace generation");
        System.out.println("------------------------");
        start = System.currentTimeMillis();
        trace.generate();
        stop = System.currentTimeMillis();
        System.out.println("Trace - generate: " + (stop - start) + "ms");
        System.out.println("-------------");
        System.out.println("Trace display");
        System.out.println("-------------");
        System.out.println("Trace - current: " + trace);
        System.out.println("--------------------");
        System.out.println("Contact Trace saving");
        System.out.println("--------------------");
        start = System.currentTimeMillis();
        trace.dumpContactTrace(new File(args[1]));
        stop = System.currentTimeMillis();
        System.out.println("Trace - saving: " + (stop - start) + "ms");
    }
}