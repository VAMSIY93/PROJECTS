
public class Edge 
{
	protected long wt;
	protected Node src;
	protected Node dst;

	public Edge(Node n1, Node n2, long weight)
	{
		this.wt = weight;
		this.src = n1;
		this.dst = n2;
	}
}
