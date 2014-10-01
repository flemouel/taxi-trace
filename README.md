# Taxi trace Analazis 
=====================

Multi-thread analysis of taxi GPS trace to extract taxi contacts.

## Usage

    java --classpath <your-class-dir> fr.insalyon.citi.trace.shanghai.LaunchContactTraceGenerator <taxi-trace-source-dir> <contact-trace-dest-file>

## Example

Extraction of a tiny part of the Shanghai taxi trace

    java fr.insalyon.citi.trace.shanghai.LaunchContactTraceGenerator ./tests/shanghai-taxi-070218-example ./tests/shanghai-taxi-070218-contacts-example

