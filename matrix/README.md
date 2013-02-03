#Multithreaded Matrix Multiplication

Generate two matrices A (size `m x n`) and B (size `n x p`) with random 
floating point values and perform matrix multiplication using multiple threads.
(each thread handles one row of the output matrix at a time).

The program outputs the amount of time it took to run, and also a MATLAB 
script that can be used to confirm that the multipication was performed 
correctly.

##Build and run
`ant` (Modify `build.xml` to change program arguments)

###Verify results in MATLAB
`run verify.m` (or whatever you chose as the output file)

Script will output either `matrices multiplied correctly` or 
`matrices not multiplied correctly`

