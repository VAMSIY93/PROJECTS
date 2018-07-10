package implementation;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.sun.istack.internal.logging.Logger;

public class Chord {
	
	public static List<Node> nodeList = new ArrayList<Node>();
	public static SortedMap<DHTKey, Node> sortedNodeMap = new TreeMap<DHTKey, Node>();
	public static Object[] sortedKeyArray;

	final static Logger log = Logger.getLogger(Chord.class); 	
	public static Node createNode(String nodeId) throws ChordException {
	
	    log.info("\n Class Chord : Inside Func createNode");
		log.info("\n Node ID = "+nodeId);
		Node node = new Node(nodeId);
	    nodeList.add(node);
	    
	    if (Chord.sortedNodeMap.get(node.getNodeKey()) != null) {
			try {
				throw new ChordException("Duplicated Key: " + node);
			} catch (ChordException e) {
				e.printStackTrace();
			}
		}
		Chord.sortedNodeMap.put(node.getNodeKey(),node);
		log.info("Added Node to sortedNodeMap");
	     
	    return node;
	    //node.startThread(out); 
	}

	public static Node getNode(int i) {
		return (Node) nodeList.get(i);
	}

	public static Node getSortedNode(int i) {
		sortedKeyArray = sortedNodeMap.keySet().toArray();
		return (Node) sortedNodeMap.get(sortedKeyArray[i]);
	}

	public static int size() {
		return nodeList.size();
	}
	
	public static int sortedMapSize() {
		return sortedNodeMap.size();
	}
}
