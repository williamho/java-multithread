import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.net.URL;
import javax.imageio.ImageIO;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.LinkedList;
import java.awt.image.BufferedImage;

public class ImageProducer implements Runnable {
	private LinkedBlockingQueue<BufferedImageFile> imageQueue;
	private int numImages, offset;
	private LinkedList<URL> imageURLs;

	ImageProducer(LinkedBlockingQueue<BufferedImageFile> imageQueue, 
		int numImages, int page) 
	{
		this.imageQueue = imageQueue;	
		this.numImages = numImages;
		this.offset = page*numImages+1;
		this.imageURLs = new LinkedList<URL>();
		fetchImageURLs();
	}

	/* Go through the image URLs in the imageURLs linked list and 
	   puts BufferedImageFile objects in the imageQueue to be read by consumers */
	public void run() {
		URL currentURL;
		BufferedImageFile img;
		String filename, urlString;
		int slashIndex;

		try {
			while (imageURLs.size() > 0) {
				currentURL = imageURLs.pop();
				urlString = currentURL.toString();
				slashIndex = urlString.lastIndexOf('/');
				filename = urlString.substring(slashIndex+1);
				img = new BufferedImageFile(ImageIO.read(currentURL),filename);
				imageQueue.put(img);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* Fetches image URLs from Picasa API and pushes URL objects 
	   to the imageURLs linked list */
	private void fetchImageURLs() {
		try {
			String urlString = "https://picasaweb.google.com/data/feed/api/all?";
			urlString += "max-results="+numImages;
			urlString += "&start-index="+offset;

			// Get XML from Picasa API
			URL url = new URL(urlString);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(url.openStream());
			doc.getDocumentElement().normalize();

			// Parse XML to obtain image URLs
			NodeList nList = doc.getElementsByTagName("content");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element eElement = (Element) nNode;
				String imageUrlString = eElement.getAttribute("src");

				// Push the image URLs on the queue to be used by run()
				imageURLs.push(new URL(imageUrlString));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

    public static void main(String[] args){
        LinkedBlockingQueue<BufferedImageFile> imageQueue = 
			new LinkedBlockingQueue<BufferedImageFile>();
		ImageProducer producer = new ImageProducer(imageQueue,5,0);
        producer.run();
    }
}

