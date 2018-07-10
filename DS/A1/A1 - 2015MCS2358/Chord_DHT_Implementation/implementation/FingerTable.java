package implementation;
import com.sun.istack.internal.logging.Logger;

import implementation.Node;

public class FingerTable {

	Finger[] fingers;

	final static Logger logger = Logger.getLogger(FingerTable.class); 
	
	public FingerTable(Node node) {
		this.fingers = new Finger[HashFunc.KEY_LENGTH];
		for (int i = 0; i < fingers.length; i++) {
			
			DHTKey start = node.getNodeKey().createStartKey(i);
			fingers[i] = new Finger(start,node);
			fingers[i].setNode(node);
		}
	}

	public Finger getFinger(int i) {
		return fingers[i];
	}

	public int size() {
		return fingers.length;
	}
}
