import java.io.Serializable;
import java.util.ArrayList;

/*has list of peers with which the server is interacting with
 */
public class PeerList implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ArrayList<PeerStuff> peerList=new ArrayList<PeerStuff>();
	
	synchronized public void updatePeerList(PeerList newPeerList){
		peerList=newPeerList.getPeerList();
	}
	/**stores peer information in this arraylist
	
	 */
	synchronized public ArrayList<PeerStuff> getPeerList(){
		return peerList;
		
	}
	/**gets arraylist's size
	 * @param none
	 * @return int
	 */
	synchronized public int getSize(){
		return peerList.size();
	}
	synchronized public void printPeerList(){
		System.out.println("We have "+peerList.size()+" elements:");
        for (PeerStuff p:peerList){
     	   p.print();
        }
	}
	/**adds object to arraylist
	 * @param peer object
	 * @return none
	 */
	synchronized public void addPeer(PeerStuff peer){
		peerList.add(peer);
		System.out.println("Add peer succeed. Current List:");
		printPeerList();
	}
	/**gets IP address
	 * @param int i
	 * @return none
	 */
	synchronized public String getiIP(int i){
		return peerList.get(i).getIP();
	}
	/**gets port
	 * @param int i
	 * @return none
	 */
	synchronized public int getiPort(int i){
		return peerList.get(i).getPort();
	}

}