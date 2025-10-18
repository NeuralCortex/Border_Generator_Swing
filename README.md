# Border Generator Swing 1.1.0

![Application Screenshot](https://github.com/NeuralCortex/Border_Generator_Swing/blob/main/app.png)

## Overview

Border Generator Swing is a Java-based application built with the Swing framework, designed to create standardized border lines. It enables users to generate boundary data globally using OpenStreetMap and export it in CSV format.

## Requirements

- An active internet connection is required.
- The application can be launched by double-clicking the JAR file.
- Java Runtime Environment (JRE) or Java Development Kit (JDK) version 24 is required, as Swing is included by default.

## Usage Instructions

### Tab 1: Generating Boundary Data

1. Right-click on the map to select a country or state from the GEO Information table.
2. Specify the desired X-kilometer lines by setting the number and stepping.
3. Save the generated boundary data as a CSV file (saved in the `CSV` directory).

### Tab 2: Processing Boundary Intersections

1. Import the first boundary line (e.g., `France.040.csv`).
2. Import the second boundary line (e.g., `Spain.000.csv`).
3. Identify the first intersection point (marked by red and blue lines) and select it using the context menu.
4. Repeat for the second intersection point.
5. Use the context menu to trim the border to the selected section.
6. Export the trimmed border section as a CSV file.
7. The process is complete.

### Tab 3: Verifying Boundaries

- This tab allows you to visualize constructed boundaries.
- Supports simultaneous display of CSV and HCM format files.
- Right-click on the map to select two points and calculate the distance between them.

## Technologies Used

- **IDE**: [Apache NetBeans 27](https://netbeans.apache.org/)
- **Java SDK**: [Java 24](https://www.oracle.com/java/technologies/downloads/#jdk24-windows)