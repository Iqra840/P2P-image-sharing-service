import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class ImageServer {
	
	BufferedImage bufferedImage=new BufferedImage(400,400,BufferedImage.TYPE_INT_RGB);
	PeerList peerList=new PeerList();
	ArrayList<ObjectOutputStream> OutputStreams=new ArrayList<ObjectOutputStream>();
	ArrayList<PrintWriter> PrintWriters=new ArrayList<PrintWriter>();
	int[] subPixel=new int[400];
	 
	public static void main(String[] args) {
	       ImageServer imageServer = new ImageServer();
	       imageServer.go();
	       
	    }

	public void go(){

		Thread Peers = new Thread(new Peers());
        Peers.start();
        
        Thread gui = new Thread(new servergui());
        gui.start();
        
        Thread serverUploadServer=new Thread(new UploadServer());
        serverUploadServer.start();
     
	}
//peers class listens for peer connections
	public class Peers implements Runnable{
		public void run() {
			
		       try {
		       ServerSocket serverSocket = new ServerSocket(9000);
		
		       while(true) {
		          Socket socketFromNewPeer = serverSocket.accept(); //accept connection
		          //sends and waits for a port connection
		          ObjectInputStream peerInfoIn=new ObjectInputStream(socketFromNewPeer.getInputStream());
		          ObjectOutputStream peerInfoOut=new ObjectOutputStream(socketFromNewPeer.getOutputStream());
		          
		          
		          int thisPort=(int)peerInfoIn.readObject();
				  PeerStuff thisPeer=new PeerStuff(socketFromNewPeer.getInetAddress().getHostAddress().toString(),thisPort);
				  thisPeer.print();
				  peerList.addPeer(thisPeer);
		          peerInfoOut.flush();
		          peerInfoOut.close();
		          peerInfoIn.close();
		          
		          socketFromNewPeer.close();
		          
		          
		        }
		      }catch(Exception ex) {
//		         ex.printStackTrace();
		      }
			
		}
	}
	
	public class UploadServer implements Runnable{
		
		int port;
		/**uploads server info
		 * @param none
		 * @return none
		 */
		public void run(){
			
			port=9001;
			ServerSocket sox=null;
			while(sox==null&&port<65536){
				try{
					sox=new ServerSocket(port);
				}catch(IOException e) {
					port++;
				}
			}
			while(true) {
				try {
					Socket peerInSocket = sox.accept();
					
					Thread t = new Thread(new Uploader(peerInSocket));
			        t.start();
				} catch (IOException e) {

				}
		     }
			
		}
		
		public class Uploader implements Runnable{   
			Socket peerInSocket;
			ObjectOutputStream peerOut;
			public Uploader(Socket peerInSocket) throws IOException{
				this.peerInSocket=peerInSocket;
				peerOut=new ObjectOutputStream(peerInSocket.getOutputStream());
				OutputStreams.add(peerOut);
				
			}
			/*writes image
			 * @param none
			 * @return none
			 */
			public void run(){
				try{
					int randi=new Random().nextInt(20);
					int randj=new Random().nextInt(20);
					while (true){
						Thread.sleep(150);
						peerOut.reset();
				        int[] subPixel=new int[400];
				        if (randi<19) randi++; 
				        else if (randj<19) {randi=0; randj++;}
				        else {randi=0;randj=0;}
				       subPixel=bufferedImage.getRGB(randi*20,randj*20,20,20,subPixel,0,20);
						peerOut.writeObject(new Images(new MiniImage(randi,randj,subPixel),peerList)); 
				        peerOut.flush();
					}

				}	
				catch (Exception ex){

				}
		
			}
		}

	}
	
	public void tellEveryone() {
	      Iterator<ObjectOutputStream> hasstuff = OutputStreams.iterator();
	      
	      while(hasstuff.hasNext()) {
	        try {
	        	
	           ObjectOutputStream thisOut=(ObjectOutputStream)hasstuff.next();
               thisOut.reset();
	           int[] subPixel=new int[400];
			   int randi=new Random().nextInt(20);
			   int randj=new Random().nextInt(20);
			   subPixel=bufferedImage.getRGB(randi*20,randj*20,20,20,subPixel,0,20);
			   thisOut.writeObject(new Images(new MiniImage(randi,randj,subPixel),peerList)); 
	           thisOut.flush();
	         } catch(Exception ex) {
	        	 //do nothing
	         }
	      
	       }// end while
	      
	       
	   } // close tellEveryone
	
	public class servergui implements Runnable{
		private JFrame frame;
		private JButton jb;
		private ImageCanvas jp;
		JFileChooser choose;
		Image currentImage;
		BufferedImage originImage=null;
		int[] pixels=new int[400*400];
		
		public void run() {
			
			try {
			    originImage = ImageIO.read(new File("Flower.png"));
			} catch (IOException e) {
			}
			Graphics g=bufferedImage.getGraphics();
			g.drawImage(originImage, 0,0,400,400,null);
			g.dispose();
			setPixel();
			/**chooses file for server
			 */
	    	choose=new JFileChooser();
			try{
				if (choose.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
					try {
					    originImage = ImageIO.read(new File(choose.getSelectedFile().getPath()));
					} catch (IOException ex) {
					}
					g=bufferedImage.getGraphics();
					g.drawImage(originImage, 0,0,400,400,null);
					g.dispose();
				}
			}catch(Exception ex){
				JOptionPane.showMessageDialog(null, "", "Picture Load failed", JOptionPane.ERROR_MESSAGE);
			}
			setPixel();
			
			frame=new JFrame();
			jb=new JButton("Load another image");
			jb.addActionListener(new fileChooserListener());
			jp=new ImageCanvas();
			frame.getContentPane().add(BorderLayout.CENTER,jp);
			frame.getContentPane().add(BorderLayout.SOUTH,jb);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(410,450);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		}
		
		public void setPixel(){
			pixels=bufferedImage.getRGB(0,0,400,400,pixels,0,400);
		}
		
		public class ImageCanvas extends Canvas{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void paint(Graphics g){
				g.drawImage(bufferedImage,5,5,this);

			}
		}
		
		public class fileChooserListener implements ActionListener{
			/**listens for file choosing
			 * @param action e
			 * @return none
			 */
			public void actionPerformed(ActionEvent e){
				choose=new JFileChooser();
				try{
					if (choose.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
						try {
						    originImage = ImageIO.read(new File(choose.getSelectedFile().getPath()));
						} catch (IOException ex) {
						}
						Graphics g=bufferedImage.getGraphics();
						g.drawImage(originImage, 0,0,400,400,null);
						g.dispose();
					}
				}catch(Exception ex){
					JOptionPane.showMessageDialog(null, "", "Pcannot load picture", JOptionPane.ERROR_MESSAGE);
				}
		        setPixel();
		        jp.repaint();
		        System.out.println("repainting image....");

			}
		}
		

	}


}
