************************************************************************
file with basedata            : c1563_.bas
initial value random generator: 1033733941
************************************************************************
projects                      :  1
jobs (incl. supersource/sink ):  18
horizon                       :  126
RESOURCES
  - renewable                 :  2   R
  - nonrenewable              :  2   N
  - doubly constrained        :  0   D
************************************************************************
PROJECT INFORMATION:
pronr.  #jobs rel.date duedate tardcost  MPM-Time
    1     16      0       22        8       22
************************************************************************
PRECEDENCE RELATIONS:
jobnr.    #modes  #successors   successors
   1        1          3           2   3   4
   2        3          3           6  14  15
   3        3          2          11  16
   4        3          2           5   8
   5        3          3           6   7   9
   6        3          2          11  13
   7        3          1          13
   8        3          2          10  17
   9        3          1          10
  10        3          1          15
  11        3          1          12
  12        3          1          17
  13        3          1          16
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
  2      1     2       4    8    8    9
         2     8       2    8    8    5
         3    10       1    3    6    4
  3      1     1       7   10    3    4
         2     1       8   10    2    4
         3     4       3   10    2    4
  4      1     4       5    6    7    4
         2     5       5    5    7    4
         3     9       4    1    5    4
  5      1     6       5    6    3    5
         2     7       2    3    2    4
         3     7       4    2    3    2
  6      1     5       5    4    3    8
         2     6       4    2    3    7
         3     9       3    2    2    6
  7      1     6       4    7    5    2
         2     7       3    4    2    1
         3    10       2    3    2    1
  8      1     4       8    8    8    7
         2     5       5    6    6    6
         3     9       4    5    2    5
  9      1     1       3    9    9    6
         2     2       2    8    7    5
         3     7       2    6    3    2
 10      1     6       7    6    8    8
         2     8       6    5    6    6
         3     9       4    4    4    5
 11      1     3       6    7    5    9
         2     3       5    6    5   10
         3    10       4    4    5    9
 12      1     3       7    6   10   10
         2     7       5    3    8    7
         3     7       6    1    5    7
 13      1     2       6    8    5    8
         2     4       5    7    3    8
         3     5       3    6    2    8
 14      1     5       4    9    3   10
         2     8       4    8    3   10
         3     8       4    9    3    9
 15      1     4       7    5    9    9
         2     4       8    5    7   10
         3    10       6    4    7    8
 16      1     4       9    3    3    8
         2     6       6    2    3    8
         3     9       1    2    2    8
 17      1     1       9    8    9    9
         2     2       8    7    7    9
         3     3       7    6    4    9
 18      1     0       0    0    0    0
************************************************************************
RESOURCEAVAILABILITIES:
  R 1  R 2  N 1  N 2
   20   21   98  118
************************************************************************
