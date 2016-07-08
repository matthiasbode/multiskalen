************************************************************************
file with basedata            : c1526_.bas
initial value random generator: 445142930
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
    1     16      0       19        0       19
************************************************************************
PRECEDENCE RELATIONS:
jobnr.    #modes  #successors   successors
   1        1          3           2   3   4
   2        3          1          15
   3        3          2           5   9
   4        3          3           6   8  10
   5        3          1          17
   6        3          1           7
   7        3          2           9  12
   8        3          2          12  13
   9        3          2          13  14
  10        3          2          11  16
  11        3          1          15
  12        3          1          15
  13        3          1          16
  14        3          2          16  17
  15        3          1          18
  16        3          1          18
  17        3          1          18
  18        1          0        
************************************************************************
REQUESTS/DURATIONS:
jobnr. mode duration  R 1  R 2  N 1  N 2
------------------------------------------------------------------------
  1      1     0       0    0    0    0
  2      1     2       0    7    9    0
         2     4       4    0    7    0
         3     5       0    4    0    2
  3      1     6       0    6    0    8
         2     8       0    6    0    4
         3     9       5    0    7    0
  4      1     1       5    0    0    8
         2     2       3    0    7    0
         3     3       2    0    0    4
  5      1     5       0    8    0    6
         2     5       6    0    1    0
         3    10       5    0    0    6
  6      1     5       2    0    7    0
         2    10       2    0    0    7
         3    10       2    0    5    0
  7      1     6       4    0   10    0
         2     7       4    0    9    0
         3     9       0    7    0   10
  8      1     6       8    0    8    0
         2     7       0    5    6    0
         3     8       8    0    4    0
  9      1     2       0    1    0    8
         2     8       6    0    0    6
         3    10       3    0    8    0
 10      1     3       0    7    0    6
         2     4       4    0    7    0
         3     4       0    7    8    0
 11      1     4       0    6    0    7
         2     8       0    4    0    5
         3    10       0    3    3    0
 12      1     5       9    0    3    0
         2     7       8    0    0    9
         3     8       0    5    3    0
 13      1     1       0    8    7    0
         2     4       0    7    0    6
         3     7       0    7    0    5
 14      1     1       5    0    0    3
         2     4       5    0    9    0
         3     6       0    1    4    0
 15      1     2       0    7    0    6
         2     3       0    2    5    0
         3     7       5    0    0    6
 16      1     2       0   10    0    7
         2     7       7    0    5    0
         3    10       0    8    2    0
 17      1     2       0    8    0    4
         2     5       0    7    7    0
         3     7       0    3    6    0
 18      1     0       0    0    0    0
************************************************************************
RESOURCEAVAILABILITIES:
  R 1  R 2  N 1  N 2
   13   17  104   97
************************************************************************
