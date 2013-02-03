import java.util.Random;
import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;

class MatrixMultiply implements Runnable {
	private float[][] A, B, outMatrix;
	private static final float maxVal = 10;
	private AtomicInteger rowNum = new AtomicInteger(0);

	MatrixMultiply(float[][] A, float[][] B) {
		this.A = A;
		this.B = B;
		this.outMatrix = new float[A.length][B[0].length];
		randomizeMatrix(A);
		randomizeMatrix(B);
	}

	public void run() {
		int currRow = rowNum.getAndIncrement();
		// Each thread calculates one row of the output matrix at a time
		for (; currRow < A.length; currRow = rowNum.getAndIncrement()) {
			for (int currCol=0; currCol<B[0].length; currCol++) {
				outMatrix[currRow][currCol] = calculateElement(currRow,currCol);
			}
		}
	}

	// Calculate the value of an element in the output matrix
	public float calculateElement(int row, int col) {
		float retVal = 0;
		for (int i=0; i<B.length; i++) 
			retVal += A[row][i] * B[i][col];
		return retVal;
	}

	// Randomize values in matrix
	public void randomizeMatrix(float[][] M) {
		Random rand = new Random();
		for (int row=0; row<M.length; row++) {
			float[] rowVec = M[row];
			for (int col=0; col<rowVec.length; col++) {
				M[row][col] = rand.nextFloat()*maxVal;
			}
		}
	}

	// Write output matrix to output file (matlab notation)
	public void writeOutputMatrix(BufferedWriter out) 
		throws IOException
	{
		writeMatrix(out,outMatrix);
	}

	// Write matrix to output file (matlab notation)
	public static void writeMatrix(BufferedWriter out, float[][] M) 
		throws IOException
	{
		out.write("[");
		for (float[] rowVec : M) {
			for (float i : rowVec) {
				out.write(i + " ");
			}
			out.write("; ");
		}
		out.write("]");
	}

	public static void main(String args[]) 
		throws InterruptedException, IOException 
	{
		int m = 0, n = 0, p = 0;
		int numThreads = 1;
		Thread[] threads = null;
		FileWriter fstream = null;
		BufferedWriter out = null;
		try {
			m = Integer.parseInt(args[0]);
			n = Integer.parseInt(args[1]);
			p = Integer.parseInt(args[2]);
			numThreads = Integer.parseInt(args[3]);
			threads = new Thread[numThreads];
			fstream = new FileWriter(args[4]);
			out = new BufferedWriter(fstream);
			if (m <= 0 || n <= 0 || p <= 0)
				throw new Exception();
		}
		catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println(
				"usage: m n p numThreads filename\n\n" + 
				"Multiply a matrix A (m x n) with a matrix B (n x p).\n" +
				"A and B will be randomly generated and the results saved " +
				"to a matlab script that can be run to verify results.\n"
			);
			System.exit(-1);
		}
		// Allocate the arrays
		float[][] A = new float[m][n];
		float[][] B = new float[n][p];
		MatrixMultiply mm = new MatrixMultiply(A,B);

		long start = System.currentTimeMillis();

		// Start the threads
		for (int i=0; i<numThreads; i++) {
			threads[i] = new Thread(mm);
			threads[i].start();
		}

		// Wait for threads to finish
		for (int i=0; i<numThreads; i++) 
			threads[i].join();
	
		float duration = (System.currentTimeMillis() - start);
		System.out.println("Done in " + duration / 1000 + "s");

		// Write matlab script that can be run to check correctness
		out.write("A = ");
		writeMatrix(out,A);
		out.write(";\n");
		out.write("B = ");
		writeMatrix(out,B);
		out.write(";\n");
		out.write("result = ");
		mm.writeOutputMatrix(out);
		out.write(";\n");
		out.write(
			"if (abs(A*B - result) <= 1e-2)\n" + 
			"	disp('matrices multiplied correctly');\n" +
			"else\n" +
			"	disp('matrices not multiplied correctly');\n" + 
			"end\n"
		);
		out.close();
	}
}

