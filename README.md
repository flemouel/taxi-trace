# Taxi Trace Analysis 

Multi-thread analysis of taxi GPS trace to extract taxi contacts.

## Usage

    java --classpath <your-class-dir> fr.insalyon.citi.trace.shanghai.LaunchContactTraceGenerator <taxi-trace-source-dir> <contact-trace-dest-file>

*Note*: for large trace analysis, JVM initial and max memory allocation parameters are strongly recommended to modify (e.g. `-Xms1024m -Xmx16384m`) 

## Example

Extraction of a tiny part of the Shanghai taxi trace

    java fr.insalyon.citi.trace.shanghai.LaunchContactTraceGenerator ./tests/shanghai-taxi-070218-example ./tests/shanghai-taxi-070218-contacts-example

## Contact parameters

The contact parameters can can customized in `Trace` class

    Trace.DISTANCE_RANGE = 250; // meters
    Trace.TIME_RANGE = 30; // seconds

Here, a contact exists if two taxis are in 250 meters range within the last 30s.

## Customizing

For long running execution, a progress bar can be displayed in `Trace` class.

    Trace.VERBOSE = true; //default

The distance between two GPS coordinates can be computed in `Coordinate` class
according to three metrics:

    public double distance(Coordinate coordinate) {
        return distanceHaversine(coordinate);
    }

    // Accuracy: -, Efficiency: ~, Order magnitude: 1
    public double distanceGeometric(Coordinate coordinate);

    // Accuracy: +, Efficiency: +, Order magnitude: 0.5    
    public double distanceHaversine(Coordinate coordinate);

    // Accuracy: ++, Efficiency: --, Order magnitude: 2
    public double distanceVincenty (Coordinate coordinate);

The contact trace computation can be modified in `Trace` class to be mono-thread or multi-thread

    public void generate() {
        generateMultiThread();
        // generateSingleThread();
    }

## Contributors

This project is being developed as part of the research activities of the
[DynaMid](http://dynamid.citi-lab.fr/) group of the
[CITI Laboratory](http://www.citi-lab.fr/) at
[INSA-Lyon](http://www.insa-lyon.fr/), in collaboration with 
the [CoopIS Lab](http://coopis.sjtu.edu.cn:8080/cisg/) & [Network Lab](http://www.cs.sjtu.edu.cn/~yzhu/nrl/),
[CS Department](http://www.cs.sjtu.edu.cn),
[SEIEE](http://english.seiee.sjtu.edu.cn) at
[SJTU](http://en.sjtu.edu.cn).

Frédéric Le Mouël ([@flemouel](https://twitter.com/flemouel)), Guanghsuo Chen

## License

    Copyright 2013-2014 Institut National des Sciences Appliquées de Lyon (INSA-Lyon)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
 
         http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

