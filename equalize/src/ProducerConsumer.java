import java.util.concurrent.*;


public class ProducerConsumer {
	
	private static int cores = Runtime.getRuntime().availableProcessors();
	private static final int nImages = 10;
	private static int nProds = cores;
	private static int nCons = 1;
	
	public static void main(String[] args) {
		SynchronizedCounter counter = new SynchronizedCounter(nImages);
		ExecutorService executorP = Executors.newFixedThreadPool(nProds);
		ExecutorService executorC = Executors.newFixedThreadPool(nCons);
		LinkedBlockingQueue<BufferedImageFile> imageQueue = new LinkedBlockingQueue<BufferedImageFile>();
		long startTime = System.currentTimeMillis();
		for(int i = 0; i < nProds-1; i++){
	        ImageProducer producer = new ImageProducer(imageQueue, nImages/nProds, i);
	        executorP.execute(producer);
		}
        ImageProducer producer = new ImageProducer(imageQueue, nImages-(nImages/nProds)*(nProds-1), nProds-1);
        executorP.execute(producer);
		for(int i = 0; i < nCons; i++){
			ImageConsumer consumer = new ImageConsumer(imageQueue, counter);
			executorC.execute(consumer);
		}
        
        executorP.shutdown();
        try {
        	  if (!executorP.awaitTermination(60, TimeUnit.SECONDS)) {
        	    //pool didn't terminate after 60 seconds
        	    executorP.shutdownNow();
        	  }
        	} catch (InterruptedException ex) {
        	  executorP.shutdownNow();
        	  Thread.currentThread().interrupt();
        	}
        	
        executorC.shutdown();
        try {
        	  if (!executorC.awaitTermination(60, TimeUnit.SECONDS)) {
        	    //pool didn't terminate after 60 seconds
        	    executorC.shutdownNow();
        	  }
        	} catch (InterruptedException ex) {
        	  executorC.shutdownNow();
        	  Thread.currentThread().interrupt();
        	}
        long estimatedTime = System.currentTimeMillis() - startTime;
        
        System.out.println("Finished all threads. Elapsed time was " + estimatedTime);
        

	}
}
