# Border Generator Swing 1.0.0

![image](https://github.com/NeuralCortex/Border_Generator_Swing/blob/main/app.png)

## How the program works

The Border Generator is a Swing project that allows you to create border lines in a standardized way for the first time.</br>
The boundary data can be generated worldwide using OpenStreetMap and saved in CSV format.</br>

## A notice

An existing internet connection is mandatory.</br>
Unlike the JavaFX project, the program can be started by double-clicking the JAR file.</br>
All you need to do is have a JRE or JDK version 24 installed; Swing is included there as standard.

## Work steps

### First tab

1. Right click on the country whose border you want to use. (State or Country can now be selected in the GEO Information table).
2. Create the desired X-kilometer lines (currently 0 to 100KM is supported).
3. Save the boundary data in CSV format (the data will be placed in the CSV directory).

### Second tab

1. Import of the first boundary line (e.g.: Germany.006.csv).
2. Import of the second boundary line (e.g.: Austria.000.csv).
3. Find the first intersection of the boundary (indicated by the red and blue lines) and press 1 on the keyboard.
4. Find the second intersection of the boundary and press 2 on the keyboard.
5. Finally, press 3 on the keyboard (The border will be clipped to the border section).
6. Export of the border section as a CSV file.
7. The process is now complete.

### Third tab

This tab is for checking the constructed boundaries. CSV and HCM format files can be displayed simultaneously.</br>
By right-clicking on the map, 2 points can be specified whose distance is calculated.

## Technology used

This Swing project was built with the Apache NetBeans 25 IDE [NetBeans 25](https://netbeans.apache.org/).

The following frameworks should be installed:

- JAVA SDK [JAVA 24](https://www.oracle.com/de/java/technologies/downloads/#jdk24-windows)
