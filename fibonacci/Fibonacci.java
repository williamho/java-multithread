import java.util.*;
import java.util.concurrent.*;

public class Fibonacci {
	public static void main(String[] args) {
		int num = 1;
		int out = 0;
		try {
			num = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println("first argument must be a whole number");
			System.exit(-1);
		}
		if (num <= 2) {
			out = 1;
		}
		else {
			ExecutorService pool = Executors.newFixedThreadPool(2);
			Callable<Integer> t1 = new FibonacciCallable(num-1);
			Callable<Integer> t2 = new FibonacciCallable(num-2);
			Future<Integer> future1 = pool.submit(t1);
			Future<Integer> future2 = pool.submit(t2);
			try {
				out = future1.get() + future2.get();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("Fibonacci number #" + num + " is " + out);
		System.exit(0);
	}

	public static class FibonacciCallable implements Callable<Integer> {
		private Integer num;
		public FibonacciCallable(Integer num) {
			this.num = num;
		}
		public Integer call() {
			return calc(num);
		}
		public Integer calc(Integer num) {
			if (num <= 2)
				return 1;
			return calc(num-1)+calc(num-2);
		}
	}
}

