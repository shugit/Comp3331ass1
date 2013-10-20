/**
 * Least Loaded Path (LLP): This algorithm tries to find the least loaded path
 * currently available from the source to the destination, where the load of a
 * path is defined to be the maximum load on any link in the path. The load on a
 * link is defined as the ratio of its current number of active virtual circuits
 * to the capacity, C , of that link for carrying virtual circuits. Note that,
 * this al gorithm ignores the number of hops and the delay associated with each
 * link. There are two main differences between LLP and the other two algorithms
 * (SHP and SDP). Firstly, the path cost in LLP is not an additive function, as
 * is the case with the other two algorithms (in SHP and SDP the cost of the
 * path is simply the sum of the cost al ong each individual link that
 * constitutes the path). Secondly, link costs (i.e. the link load) change with
 * time, whereas in both SHP and SDP the link costs are static over the entire
 * lifetime
 * 
 * @author Sephy
 * 
 */
public class LLP {
	Topology topology;
	Workload workload;

	public LLP(Topology t, Workload w) {
		topology = t;
		workload = w;
	}
}
