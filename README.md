# EVO

David's tiny evolution simulator

How to run:
```
javac *.java
java Main
```

## Instructions

- press SPACE to advance the simulation by 1 step
- press 'v' to toggle the map view between showing temperature and altitude
- press 'c' to write creature data to a file

## Interpreting DNA.txt

Pressing 'c' at any point in the sim will create a file called 'DNA.txt'. This
file contains information about the success of different creature types in the
sim.

Format:
```
XXXXX=X
```

Where the string before the equals sign is the DNA sequence and the number
after is the number of creatures with that DNA sequence that were born during
the simulation.
