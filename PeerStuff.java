import java.io.Serializable;

//has information about peers in the connection
class PeerStuff implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String IP;
	private int port;
	
	public PeerStuff(String IP,int port){
		this.IP=IP;
		this.port=port;
	}
	
	public void print(){
		System.out.println("Peer information:  "+IP+": "+port);
	}
	//gets peer IP
	/**
	 * @param none
	 * @return IP
	 */
	public String getIP(){
		return IP;
	}
	//gets port
		/**
		 * @param none
		 * @return port
		 */
	public int getPort(){
		return port;
	}

}