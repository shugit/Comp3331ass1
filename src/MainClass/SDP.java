package MainClass;

/**
 * Shortest Delay Path (SDP): This algorithm tries to find the shortest path
 * currently available from the source to the destination, where the length of
 * the path refers to the cumulative propagation delay for traversing the chosen
 * links in the path. In other words, this is Dijktra¡¯s algorithm with the cost
 * of each link set to the propagation delay. Recall that the network topology
 * file specifies the delay along each link in the network. Note that, this
 * algorithm ignores the number of hops and the load associated with each link.
 * 
 * @author Sephy
 * 
 */
public class SDP extends BasicProtocol{
	
	public SDP(Topology t, Workload w) {
		topology = t;
		workload = w;
	}
	public int getDirectDistance(String node, String target) {
		return topology.getDelay(target, node);
	}
}
