public class Fib implements Runnable {
	int curr=1, prev=1, counter=0, num;
	Fib(int num) {
		this.num = num;
	}

	private synchronized void calc() {
		try {
			System.out.println(Thread.currentThread().getName() + ": " + curr);
			int tmp = curr;
			curr += prev;
			prev = tmp;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while(counter++ < num)
			calc();
	}

	public static void main(String args[]) {
		int num = 1;
		try {
			num = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println("first argument must be a whole number");
			System.exit(-1);
		}
		Fib f = new Fib(num);
		for (int i=0; i<4; i++)
			(new Thread(f)).start();
	}
}

