import java.io.Serializable;
import java.awt.*;
public class MiniImage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int x;
	int y;
	int[] pixels;
	/**gets a section of the image to download
	 * @param port number
	 * @return none
	 */
	public MiniImage(int x, int y, int[] pixels){
		this.x=x;
		this.y=y;
		this.pixels=pixels;
	}
}