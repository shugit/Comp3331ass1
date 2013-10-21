package MainClass;

public class SHP  extends BasicProtocol{
	/**
	 * Shortest Hop Path (SHP): This algorithm tries to find the shortest path
	 * currently available from the source to the destination, where the length
	 * of a path refers to the number of hops (i.e. links) traversed. In essence
	 * this is Dijkstra¡¯s algorithm with the cost of each link set to 1. Note
	 * that, this algorithm ignores the delay and load associated with each link
	 * 
	 * @param t
	 * @param w
	 */
	public SHP(Topology t, Workload w){
		topology = t;
		workload = w;
	}

	public int getDirectDistance(String node, String target) {
		return 1;
	}


}
