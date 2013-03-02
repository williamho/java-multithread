import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.lang.Math;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.LinkedList;
import javax.imageio.ImageIO;

public class ImageConsumer implements Runnable {
    private LinkedBlockingQueue<BufferedImageFile> imageQueue;
    private SynchronizedCounter counter;

    ImageConsumer(LinkedBlockingQueue<BufferedImageFile> imageQueue, SynchronizedCounter counter){
        this.imageQueue = imageQueue;
        this.counter = counter;
    }

    public void run(){
        try {
//        	if(imageQueue.isEmpty())
//        		Thread.sleep(6000);
        	while(true){
        		if(counter.value() == counter.imageNum())
    				break;
        		BufferedImageFile image = imageQueue.take();
        		ImageHistogramEqualizer equalizer = this.new ImageHistogramEqualizer(image);
        		counter.increment();
        	}
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private class ImageHistogramEqualizer{
        private BufferedImageFile image;

        ImageHistogramEqualizer(BufferedImageFile image){
            this.image = image;
            equalize();
        }

        private void equalize(){
        	int fileNum = 0;
            int height = image.getHeight();
            int width = image.getWidth();

            float[] hsb = new float[]{0,0,0,0};
            int[] pixels = image.getRGB(0, 0, width, height, null, 0, width);
            int[] histogram = new int[256];
            int cdfMin = 0;

            int i;
            Color c;

            // generate histogram of brightness values
            for(i = 0; i < pixels.length; i++){
                c = new Color(pixels[i]);
                hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
                histogram[(int)(hsb[2]*255)]++;
            }

            // find first non-zero element of histogram
            for(i = 0; histogram[i] == 0; i++);

            cdfMin = histogram[i]; // because a cdf monotonically increases

            // convert histogram to cdf
            if(i == 0){
                i++;
            }

            for(; i < 256; i++){
                histogram[i] += histogram[i-1];
            }

            for(i = 1; i < 256; i++){
                histogram[i] = Math.round((((float) histogram[i])-cdfMin)/(height*width-cdfMin)*255);
            }

            for(i = 0; i < pixels.length; i++){
                c = new Color(pixels[i]);
                hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
                hsb[2] = ((float) (histogram[(int)(hsb[2]*255)]))/255;
                pixels[i] = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
            }

            BufferedImageFile equalizedImage = new BufferedImageFile(image);
            equalizedImage.setRGB(0, 0, width, height, pixels, 0, width);

            File outputfile1 = new File("unequalized " + image.getFilename() + ".jpg");
            File outputfile2 = new File("equalized " + image.getFilename() + ".jpg");
            
            try{
            	ImageIO.write(image, "jpg", outputfile1);
                ImageIO.write(equalizedImage, "jpg", outputfile2);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

    }

//    public static void main(String[] args){
//        LinkedBlockingQueue<BufferedImage> imageQueue = new LinkedBlockingQueue<BufferedImage>();
//        BufferedImage image = null;
//        try{
//            image = ImageIO.read(new File("Unequalized_Hawkes_Bay_NZ.jpg"));
//            imageQueue.put(image);
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//        ImageConsumer consumer = new ImageConsumer(imageQueue);
//        consumer.run();
//    }
}
