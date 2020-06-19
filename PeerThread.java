import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

//manages threads of peers
public class PeerThread implements Runnable{
		private JFrame frame;
		private Area jp;
		BufferedImage originImage=null;
		int[] pixels=new int[400*400];
		BufferedImage grid[][]=new BufferedImage[20][20];
        BufferedImage bufferedImage;
        
		public void run(){
			
			for (int i=0;i<20;i++)
				for(int j=0;j<20;j++)
					grid[i][j]=new BufferedImage(20,20,BufferedImage.TYPE_INT_RGB);
		
			Graphics g=bufferedImage.getGraphics();
			g.drawImage(originImage, 0,0,400,400,null);
			g.dispose();
			Thread guiThread = new Thread(new GUIThread());
	        guiThread.start();

			try{
				Socket soxet=new Socket("127.0.0.1",4343);
				ObjectOutputStream sOut=new ObjectOutputStream(soxet.getOutputStream());
				ObjectInputStream sIn=new ObjectInputStream(soxet.getInputStream());
				MiniImage subImage;
				while (true){
					subImage=(MiniImage)sIn.readObject();
					bufferedImage.setRGB(subImage.x*20, subImage.y*20, 20,20,subImage.pixels,0,20);
					jp.repaint();
				}
			}catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}

	
		public class Area extends Canvas{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void paint(Graphics g){
				g.drawImage(bufferedImage,5,5,this);
				setPixel();
			}
		}
		
		public void setPixel(){
			pixels=bufferedImage.getRGB(0,0,400,400,pixels,0,400);
		}
		
		public class GUIThread implements Runnable{
			//sets frame
			/**
			 * @param none
			 * @return none
			 */
	        public void run() {
	    		frame=new JFrame();
	    		jp=new Area();
	    		frame.getContentPane().add(BorderLayout.CENTER,jp);
	    		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    		frame.setSize(410,440);
	    		frame.setLocationRelativeTo(null);
	    		frame.setVisible(true);
			}
		}
		

}

