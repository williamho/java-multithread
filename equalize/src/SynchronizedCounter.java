    public class SynchronizedCounter {
        private int c = 0;
        private int nImages;
        
        SynchronizedCounter(int nImages){
        	this.nImages = nImages;
        }
        
        public synchronized void increment() {
            c++;
        }

        public synchronized void decrement() {
            c--;
        }

        public synchronized int value() {
            return c;
        }
        
        public synchronized int imageNum() {
            return nImages;
        }
    }
