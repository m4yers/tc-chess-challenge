Problem
======
The problem is to find all unique configurations of a set of normal chess pieces on a chess board
with dimensions M×N where none of the pieces is in a position to take any of the others.

Run
===
```
mvn package
java -jar target/chess-solver-1.0-SNAPSHOT.jar -M 8 -N 8 -queens 8 --solver bruteforce --debug
java -jar target/chess-solver-1.0-SNAPSHOT.jar -M 7 -N 7 -kings 2 -queens 2 -bishops 2 -knights 1 --solver threading --debug
```

Usage
=======

```
-M COLUMNS                : Number of columns
-N ROWS                   : Number of rows
-kings NUMBER             : Number of kings
-queens NUMBER            : Number of queens
-knights NUMBER           : Number of knights
-bishops NUMBER           : Number of bishops
-rooks NUMBER             : Number of rooks
-s (--solver) NAME        : Choose what solver to use, avaialbe: bruteforce, caching, threading or all (default: threading)
-d (--debug)              : Print debug information (default: true)
-p (--print)              : Print solved boards to screen (default: true)
-ps (--pool-size) SIZE    : Pool size for caching solver (default: 500)
-h (-help)                : Print help information (default: true)
```
