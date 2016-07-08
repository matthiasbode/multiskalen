************************************************************************
file with basedata            : c1557_.bas
initial value random generator: 1944501568
************************************************************************
projects                      :  1
jobs (incl. supersource/sink ):  18
horizon                       :  123
RESOURCES
  - renewable                 :  2   R
  - nonrenewable              :  2   N
  - doubly constrained        :  0   D
************************************************************************
PROJECT INFORMATION:
pronr.  #jobs rel.date duedate tardcost  MPM-Time
    1     16      0       20       12       20
************************************************************************
PRECEDENCE RELATIONS:
jobnr.    #modes  #successors   successors
   1        1          3           2   3   4
   2        3          2           5  15
   3        3          2          14  17
   4        3          2           7  15
   5        3          3           6   7  13
   6        3          1           9
   7        3          2           8  14
   8        3          1          10
   9        3          1          11
  10        3          2          12  16
  11        3          2          12  14
  12        3          1          17
  13        3          1          17
  14        3          1          16
  15        3          1          18
  16        3          1          18
  17        3          1          18
  18        1          0        
************************************************************************
REQUESTS/DURATIONS:
jobnr. mode duration  R 1  R 2  N 1  N 2
------------------------------------------------------------------------
  1      1     0       0    0    0    0
  2      1     3      10    0    6    5
         2     5       4    0    4    4
         3     8       0    6    2    4
  3      1     2       0    6    6    9
         2     5       0    5    4    8
         3    10       0    5    2    3
  4      1     2       6    0    9    4
         2     6       5    0    9    4
         3     8       4    0    8    4
  5      1     1       7    0    8    8
         2     7       6    0    8    6
         3    10       6    0    6    6
  6      1     3       0    7    8    9
         2     4       5    0    7    9
         3    10       0    6    5    8
  7      1     4       5    0    7    5
         2     6       0    9    7    4
         3     7       0    5    7    2
  8      1     3      10    0    5    9
         2     4       9    0    4    7
         3     8       0    6    1    5
  9      1     3       9    0    9   10
         2     4       0    7    5    8
         3    10       0    7    3    7
 10      1     1       0    8    7    8
         2     2       9    0    3    6
         3     5       0    7    2    3
 11      1     2       9    0    5    6
         2     4       0    7    3    2
         3     4       0    5    3    4
 12      1     6       5    0    7    4
         2     8       0    2    7    4
         3    10       4    0    4    4
 13      1     1       0    4    9    4
         2     2       3    0    8    3
         3     2       5    0    9    1
 14      1     2       7    0    8    8
         2     3       0    3    4    4
         3     8       3    0    4    2
 15      1     6       6    0    9    4
         2     9       5    0    9    3
         3    10       0    1    8    3
 16      1     4       7    0    5   10
         2     6       4    0    4    7
         3     9       0    3    1    7
 17      1     2       0    7    7   10
         2     4       9    0    6    9
         3     4       0    7    5    9
 18      1     0       0    0    0    0
************************************************************************
RESOURCEAVAILABILITIES:
  R 1  R 2  N 1  N 2
   11    9  115  113
************************************************************************
