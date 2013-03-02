import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class BufferedImageFile extends BufferedImage {
	private String filename;

	public BufferedImageFile(BufferedImage bi) {
		// via http://stackoverflow.com/a/3514297
		super(
			bi.getColorModel(),
			bi.copyData(null),
			bi.getColorModel().isAlphaPremultiplied(),
			null
		);
		setFilename("default.jpg");
	}

	public BufferedImageFile(BufferedImage bi, String filename) {
		this(bi);
		setFilename(filename);
	}
	
    public String getFilename(){
    	return filename;
    }
    
    public void setFilename(String filename){
    	this.filename = filename;
    }
    
}
