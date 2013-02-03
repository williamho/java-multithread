#Multithreaded Fibonacci
Probably not a great idea in general. Files could probably be named better.

* `Fib.java`: 
Non-recursive Fibonacci. Not any more efficient than a single-threaded version, 
since threads depend on the results calculated by other threads.

* `Fibonacci.java`: 
Recursive Fibonacci with two threads. One thread calculates n-1 recursively and 
the other calculates n-2 recursively, and the results are added.

## Build and Run
	javac Fib.java && java -classpath . Fib <number> 
	javac Fibobacci.java && java -classpath . Fibonacci <number>
