import java.awt.BorderLayout;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.math.BigInteger; 
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.util.concurrent.TimeUnit;
public class ImagePeer {
	BufferedImage bufferedImage=new BufferedImage(400,400,BufferedImage.TYPE_INT_RGB);
	private ImageCanvas panelling=new ImageCanvas();
	int[] pixels=new int[400*400];
	int[] subPixel=new int[400];
	ArrayList<ObjectOutputStream> Output=new ArrayList<ObjectOutputStream>();
	
	PeerList peerList=new PeerList();
	int peer1Port=9001;
	int myPort;
	String myIP;
	String IP;
	/**main prog
	 * @param String[] args
	 * @return none
	 */
	public static void main(String[] args){
		ImagePeer imagePeer=new ImagePeer();
		imagePeer.go();

	}
	/**sets port
	 * @param port number
	 * @return none
	 */
	synchronized public void settingPort(int port){
		System.out.println("Port number"+port);
		myPort=port;
	}
	/**sets IP
	 * @param string IP
	 * @return none
	 */
	synchronized public void settingIP(String IP){
		myIP=IP;
	}
	/**returns port no.
	 * @param none
	 * @return myPort
	 */
	synchronized public int returning(){
		return myPort;
	}
	/**sets port
	 * @param none
	 * @return IP
	 */
	synchronized public String returningIP(){
		return myIP;
	}
	public void go(){
		
		//IP string for storing IP address
		String IP = JOptionPane.showInputDialog(null,
				  "Connect to server:",
				  "Input IP Address",
				  JOptionPane.QUESTION_MESSAGE);
		//username field for storing username
		String username = JOptionPane.showInputDialog(null,
				  "Input username",
				  "username:",
				  JOptionPane.QUESTION_MESSAGE);
		//password field for storing password
		String password = JOptionPane.showInputDialog(null,
				  "Input password",
				  "password:",
				  JOptionPane.QUESTION_MESSAGE);
	
		//this thread downloads the image
		Thread downloading=new Thread(new Downloader(IP,9001));
		downloading.start();
		//this thread asks for username
		Thread usernameThread=new Thread(new Loginuser(username));
		usernameThread.start();
		//this thread asks for and stores password
		Thread passwordThread=new Thread(new Loginpwd(password));
		passwordThread.start();
		//this thread starts the GUI
		Thread guiThread = new Thread(new GUIThread());
        guiThread.start();
		//this thread uploads image on the peer's canvas
		Thread peerServer=new Thread(new UploadServer());
		peerServer.start();
		//this thread connects to server
		Thread connectToServer=new Thread(new ConnectToServer());
		connectToServer.start();
		
	
		
		
	}
	//asks for login details for user
	public class Loginuser implements Runnable{
		String logindetails;
		String infoMessage="login failed", titleBar="Login failed";
		public Loginuser(String logindetails) {
		this.logindetails=logindetails;
		}
		/**asks for username
		 * @param none
		 * @return none
		 */
		public void run(){
			try {
				
				ArrayList<String> usrnm = new ArrayList<String>(); 
				
				usrnm.add("cbchan");
				usrnm.add("cjli");
				usrnm.add("mqpeng");
				boolean found = usrnm.contains(logindetails); 
				  
		        if (found) {
		            System.out.println("user found"); 
		        }
		        else {
		        	
		        	JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
		        //	TimeUnit.SECONDS.sleep(1);
		        	System.exit(0);}
		        } catch(Exception ex) {
//		        	System.out.println(ex.getMessage());
//		           ex.printStackTrace();
		        }
		}

	
	
	}
	//asks for password
	public class Loginpwd implements Runnable{
		String infoMessage="login failed", titleBar="Login failed";
		String pw;
		
		/**
		 * @param String pw
		 * @return none
		 */
		public Loginpwd(String pw){
			this.pw=pw;
			
		}
		public void run(){
			ArrayList<String> encryptedPassword = new ArrayList<String>();
			encryptedPassword.add("9d8814d33e6ebb43f7864881c37819fffcd87");
			encryptedPassword.add("4bae614bd712d6b371b32465e81579ec15e43eb");
			encryptedPassword.add("ef4faa8b853d3e28d78867ff15afe7fa3e9cc28");
			try {
				MessageDigest md = MessageDigest.getInstance("SHA-1"); 
		
			    
          
            byte[] messageDigest = md.digest(this.pw.getBytes()); 
  
            // Convert byte array into signum representation 
            BigInteger no = new BigInteger(1, messageDigest); 
  
            // Convert message digest into hex value 
            String hashtext = no.toString(16); 
  
            // Add preceding 0s to make it 32 bit 
            while (hashtext.length() < 32) { 
                hashtext = "0" + hashtext; 
            } 
            System.out.println(hashtext);
            boolean found = encryptedPassword.contains(hashtext); 
			  
	        if (found) 
	            System.out.println("user details authenticated"); 
	       // else
	    
	        //	JOptionPane.showMessageDialog(null, infoMessage, "InfoBox: " + titleBar, JOptionPane.INFORMATION_MESSAGE);
	        	//TimeUnit.SECONDS.sleep(1);
	        	//System.exit(0);

            
				
				
			
				
		        } catch(Exception ex) {
		        	//do nothing
		        }
		}
	
	}
	public class ConnectToServer implements Runnable{  
		/**connects to server
		 * @param none
		 * @return none
		 */
		synchronized public void run(){
			try {
					
		           Socket sox = new Socket(IP, 9000);
		           ObjectOutputStream peerInfoOut=new ObjectOutputStream(sox.getOutputStream());
		           ObjectInputStream peerInfoIn=new ObjectInputStream(sox.getInputStream());

		           settingIP(sox.getLocalAddress().getHostAddress());
		           peerInfoOut.writeObject(returning());
		           
		           peerInfoIn.close();
		           peerInfoOut.close();
		           
		           sox.close();
		           
		          
		        } catch(Exception ex) {
//		           ex.printStackTrace();
		        }
		}
	}
	//uploads from server
	public class UploadServer implements Runnable{
		
		int port;
		/**uploads from server
		 * @param none
		 * @return none
		 */
		public void run(){
			port=9001;
			ServerSocket ss=null;
			while(ss==null&&port<65536){
				try{
					ss=new ServerSocket(port);
				}catch(IOException e) {
					port++;
				}
			}
			settingPort(port);
			while(true) {
		        Socket peerInSocket;
				try {
					peerInSocket = ss.accept();
//					ObjectOutputStream peerOut=new ObjectOutputStream(peerInSocket.getOutputStream());
//					Output.add(peerOut);
					Thread t = new Thread(new Uploader(peerInSocket));
			        t.start();
				} catch (IOException e) {
//					e.printStackTrace();
				}
		     }
			
		}
		public int returnPort(){
			System.out.println("return port number "+port);
			return port;
		}
		
		public class Uploader implements Runnable{
			Socket peerInSocket;
			ObjectOutputStream peerOut;
			
			public Uploader(Socket peerInSocket) throws IOException{
				this.peerInSocket=peerInSocket;
				peerOut=new ObjectOutputStream(peerInSocket.getOutputStream());
				
			}
			public void run(){
				try{
					System.out.println("sending data to port:"+peerInSocket.getPort());

					int xaxis=new Random().nextInt(20);
					int yaxis=new Random().nextInt(20);
					while (true){
						Thread.sleep(400);
						peerOut.reset();

				        int[] subPixel=new int[400];
				        if (xaxis<19) xaxis++; 
				        else if (yaxis<19) {xaxis=0; yaxis++;}
				        else {xaxis=0;yaxis=0;}
					    subPixel=bufferedImage.getRGB(xaxis*20,yaxis*20,20,20,subPixel,0,20);
						peerOut.writeObject(new Images(new MiniImage(xaxis,yaxis,subPixel),peerList));   
					}
				}catch (Exception ex){
//					ex.printStackTrace();
				}
				
			}
		}

	}
	//downloads image
	public class Downloader implements Runnable{
		String serverIP;
		int serverPort;
		/**downloads image
		 * @param a string and an int
		 * @return none
		 */
		public Downloader(String serverIP,int serverPort){
			this.serverIP=serverIP;
			this.serverPort=serverPort;
		}
		public void run(){
			try {
		           Socket sox = new Socket(serverIP, serverPort);
//		           System.out.println("P2P Downloader set to download from "+sock.getLocalAddress()+": "+sock.getLocalPort()+" to "+serverIP+": "+serverPort);
		           System.out.println("P2P Downloader set to download from "+serverIP+": "+serverPort);
		           
		           ObjectInputStream peerInfoIn=new ObjectInputStream(sox.getInputStream());
		           ObjectOutputStream peerInfoOut=new ObjectOutputStream(sox.getOutputStream());
		           
		           while (true){
	        		   Images newBlock=(Images)peerInfoIn.readObject();
//	        		
	        		   PeerList newPeerList=newBlock.getPeerList();
	        		   if (newPeerList!=null&&peerList.getSize()<newPeerList.getSize())
	        				   moreDownloads(newBlock.getPeerList());
	        		   MiniImage subImage=newBlock.getImageBlock();
	        		   bufferedImage.setRGB(subImage.x*20, subImage.y*20, 20,20,subImage.pixels,0,20);
//	        		  
      				   panelling.repaint();
//      			
        		   }
                   
		        } catch(Exception ex) {
//		        	System.out.println(ex.getMessage());
//		           ex.printStackTrace();
		        }
		}
	}
	//downloads futher mini images
	/**
	 * @param an ArrayList
	 * @return none
	 */
	synchronized public void moreDownloads(PeerList newPeerList){

		
		for(int i=peerList.getSize();i<newPeerList.getSize();i++){
			if (newPeerList.getiIP(i)!=myIP&&newPeerList.getiPort(i)!=myPort){
//				System.out.println("I'm "+myPort+" I'm starting Downloader "+newPeerList.getiIP(i)+" "+newPeerList.getiPort(i));
			    try{
					Thread newDownloader=new Thread(new Downloader(newPeerList.getiIP(i),newPeerList.getiPort(i)));
					newDownloader.start();
			    }catch (Exception ex){ }
			}
			
		}
	
		System.out.println("list of " + myPort + "updated");
		peerList.updatePeerList(newPeerList);
		
	}
	//gui thread
	public class GUIThread implements Runnable{
		private JFrame frame;
		BufferedImage originImage=null;
		BufferedImage grid[][]=new BufferedImage[20][20];
		/**draws on grid
		 * @param none
		 * @return none
		 */
		public void run(){
			
			for (int i=0;i<20;i++)
				for(int j=0;j<20;j++)
					grid[i][j]=new BufferedImage(20,20,BufferedImage.TYPE_INT_RGB);
			try {
			    originImage = ImageIO.read(new File("Default.png"));
			} catch (IOException e) {
			}
			Graphics g=bufferedImage.getGraphics();
			g.drawImage(originImage, 0,0,400,400,null);
			g.dispose();
			
			frame=new JFrame();
    		frame.getContentPane().add(BorderLayout.CENTER,panelling);
    		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		frame.setSize(410,450);
    		frame.setLocationRelativeTo(null);
    		frame.setVisible(true);
		}

		public void setPixel(){
			pixels=bufferedImage.getRGB(0,0,400,400,pixels,0,400);
		}

	}
	
	public class ImageCanvas extends Canvas{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		synchronized public void paint(Graphics g){

			g.drawImage(bufferedImage,5,5,this);
			setPixel();

		}
	}
	
	public void setPixel(){
		pixels=bufferedImage.getRGB(0,0,400,400,pixels,0,400);
	}
	//outputs result on console
	/**sets port
	 * @param xaxis and yaxis
	 * @return none
	 */
	public void writeOut(int xaxis, int yaxis) {
	      Iterator<ObjectOutputStream> it = Output.iterator();
	      System.out.println("We have "+Output.size()+" elements");
	      while(it.hasNext()) {
	        try {
	           ObjectOutputStream thisOut=(ObjectOutputStream)it.next();
	           thisOut.reset();
 
	           int[] subPixel=new int[400];
			   
			   subPixel=bufferedImage.getRGB(xaxis*20,yaxis*20,20,20,subPixel,0,20);
			   thisOut.writeObject(new Images(new MiniImage(xaxis,yaxis,subPixel),peerList));  
	           thisOut.flush();
	         } catch(Exception ex) {
           //do nothing
	         }
	      
	       } // end while
	      

	       
	   } // close method


}
