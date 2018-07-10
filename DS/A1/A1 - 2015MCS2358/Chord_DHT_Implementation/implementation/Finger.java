package implementation;

public class Finger {

	Node node;
	
	DHTKey start;
	
	int lookupCount=0;
	
	int lookupTime=0;
	
	public Finger(DHTKey start, Node node) {
		this.node = node;
		this.start = start;
	}
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public int getLookupCount() {
		return this.lookupCount;
	}
	public void setLookupCount(int count) {
		this.lookupCount = count;
	}

	public int getLookupTime() {
		return this.lookupTime;
	}

	public void setLookupTime(int time) {
		this.lookupTime = time;
	}
	
	public DHTKey getStart() {
		return start;
	}

	public void setStart(DHTKey start) {
		this.start = start;
	}
}
