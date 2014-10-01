# Taxi Trace Analyzis 

Multi-thread analysis of taxi GPS trace to extract taxi contacts.

## Usage

    java --classpath <your-class-dir> fr.insalyon.citi.trace.shanghai.LaunchContactTraceGenerator <taxi-trace-source-dir> <contact-trace-dest-file>

## Example

Extraction of a tiny part of the Shanghai taxi trace

    java fr.insalyon.citi.trace.shanghai.LaunchContactTraceGenerator ./tests/shanghai-taxi-070218-example ./tests/shanghai-taxi-070218-contacts-example

# Contributors

This project is being developed as part of the research activities of the
[DynaMid](http://dynamid.citi-lab.fr/) group of the
[CITI Laboratory](http://www.citi-lab.fr/) at
[INSA-Lyon](http://www.insa-lyon.fr/), in collaboration with 
the [CoopIS Lab](http://coopis.sjtu.edu.cn:8080/cisg/) & [Network Lab](http://www.cs.sjtu.edu.cn/~yzhu/nrl/),
[Computer Science Department](http://www.cs.sjtu.edu.cn),
[School of Electronic, Information and Electrical Engineering](http://english.seiee.sjtu.edu.cn) at
[Shanghai Jiao Tong University](http://en.sjtu.edu.cn).

Frédéric Le Mouël, Guanghsuo Chen

# License

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

