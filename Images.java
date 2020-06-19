 
import java.io.Serializable;
public class Images implements Serializable{
	/**
	 * 
	 */
	synchronized public PeerList getPeerList(){
		return peerList;
		
	}
	synchronized public MiniImage getImageBlock(){
		return imageBlock;
		
	}
	private static final long serialVersionUID = 1L;
	PeerList peerList;
	MiniImage imageBlock;
	public Images(MiniImage imageBlock,PeerList peerList){
		this.imageBlock=imageBlock;
		this.peerList=peerList;
	}
	
	

}