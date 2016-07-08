************************************************************************
file with basedata            : c1560_.bas
initial value random generator: 2092260792
************************************************************************
projects                      :  1
jobs (incl. supersource/sink ):  18
horizon                       :  118
RESOURCES
  - renewable                 :  2   R
  - nonrenewable              :  2   N
  - doubly constrained        :  0   D
************************************************************************
PROJECT INFORMATION:
pronr.  #jobs rel.date duedate tardcost  MPM-Time
    1     16      0       10       13       10
************************************************************************
PRECEDENCE RELATIONS:
jobnr.    #modes  #successors   successors
   1        1          3           2   3   4
   2        3          1          14
   3        3          1           6
   4        3          3           5   7  11
   5        3          3           6   8   9
   6        3          3          10  13  15
   7        3          1          12
   8        3          1          13
   9        3          1          14
  10        3          1          17
  11        3          2          12  14
  12        3          1          16
  13        3          1          16
  14        3          2          15  17
  15        3          1          18
  16        3          1          18
  17        3          1          18
  18        1          0        
************************************************************************
REQUESTS/DURATIONS:
jobnr. mode duration  R 1  R 2  N 1  N 2
------------------------------------------------------------------------
  1      1     0       0    0    0    0
  2      1     1       0    7    2    9
         2     2       3    0    2    5
         3     8       2    0    1    4
  3      1     1       2    0    5    8
         2     2       2    0    4    7
         3     4       0    3    4    7
  4      1     1       6    0    7    2
         2     2       0    3    6    2
         3     9       4    0    5    1
  5      1     1       0    8    5    9
         2     6       3    0    5    9
         3     9       0    8    4    8
  6      1     4       0    9    3    6
         2     5       8    0    2    6
         3     6       5    0    1    5
  7      1     5       0    4    5    6
         2     9       8    0    5    4
         3    10       6    0    4    2
  8      1     1       0    7    8    5
         2     2       6    0    6    4
         3     4       0    4    4    4
  9      1     1       5    0    8    5
         2     5       4    0    6    5
         3    10       0    8    4    5
 10      1     1       0    3    7    3
         2     5       2    0    6    3
         3     6       2    0    2    2
 11      1     1       5    0    9   10
         2     3       4    0    9    7
         3    10       2    0    8    3
 12      1     1       5    0    7    7
         2     3       4    0    7    5
         3     6       3    0    6    4
 13      1     1       7    0    2   10
         2     5       0    9    2   10
         3    10       0    8    1   10
 14      1     1       0    7    2    7
         2     1       7    0    3    7
         3     2       2    0    1    6
 15      1     4      10    0    7    7
         2     5       8    0    5    5
         3    10       8    0    4    3
 16      1     2       8    0    9    4
         2     2       0    6    7    4
         3     8       8    0    5    3
 17      1     3       2    0    8    4
         2     6       0    9    3    4
         3     6       1    0    3    4
 18      1     0       0    0    0    0
************************************************************************
RESOURCEAVAILABILITIES:
  R 1  R 2  N 1  N 2
   29   22   95  102
************************************************************************
