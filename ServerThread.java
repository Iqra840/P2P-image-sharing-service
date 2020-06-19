import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
//starting class which implements runnable
public class ServerThread implements Runnable{
	
	//declaring the frames, button, and canvas
	private JFrame frame;
	private JButton button;
	private ImgArea jp;
	JFileChooser choose;
	Image currentImage;
	BufferedImage original=null;
	BufferedImage bufferedImage=new BufferedImage(400,400,BufferedImage.TYPE_INT_RGB);
	int[] pixels=new int[400*400];
	//running each individual thread
	public void run() {
		//gets original image and draws it on the canvas
		
		Graphics g=bufferedImage.getGraphics();
		g.drawImage(original, 0,0,400,400,null);
		g.dispose();
		pixelNum();
		//opens dialog box
    	choose=new JFileChooser();
		try{
			if (choose.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
				try {
				    original = ImageIO.read(new File(choose.getSelectedFile().getPath()));
				} catch (IOException ex) {
				}
				g=bufferedImage.getGraphics();
				//draws image on canvas
				g.drawImage(original, 0,0,400,400,null);
				g.dispose();
			}
		}catch(Exception ex){
			//display error popup if image doesnt load
			JOptionPane.showMessageDialog(null, "", "Failed opening picture", JOptionPane.ERROR_MESSAGE);
		}
		
		pixelNum();
		
		frame=new JFrame();
		button=new JButton("Load another image");
		button.addActionListener(new ListeningtoFile());
		jp=new ImgArea();
		frame.getContentPane().add(BorderLayout.CENTER,jp);
		frame.getContentPane().add(BorderLayout.SOUTH,button);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(410,440);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	/**
	 * gets number of pixels for the canvas
	 * @param none
	 * @return none
	 */
	public void pixelNum(){
		pixels=bufferedImage.getRGB(0,0,400,400,pixels,0,400);
	
	}
	
	public class ImgArea extends Canvas{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void paint(Graphics g){
			g.drawImage(bufferedImage,5,5,this);

		}
	}
	/**listens for the user choosing files
	 * 
	 */
	public class ListeningtoFile implements ActionListener{
		/**listens for new file
		 * @param action e
		 * @return none
		 */
		public void actionPerformed(ActionEvent e){
			choose=new JFileChooser();
			try{
				if (choose.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
					try {
					    original = ImageIO.read(new File(choose.getSelectedFile().getPath()));
					} catch (IOException ex) {
					}
					Graphics g=bufferedImage.getGraphics();
					g.drawImage(original, 0,0,400,400,null);
					g.dispose();
				}
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null, "", "Picture Load failed", JOptionPane.ERROR_MESSAGE);
			}
	        pixelNum();
	        jp.repaint();
	        System.out.println("Image pixel changed; we are repainting");
		}
	}
	

}
