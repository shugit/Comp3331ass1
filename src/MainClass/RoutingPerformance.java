package MainClass;

/**
 * argu[3]:SHP/SDP/LLP,TOPOLOGY_FILE,WORKLOAD_FILE
 * TOPOLOGY: A B 10 19 = A和B之间,10ms延迟,最大19个同事虚拟电路
 * your program must use the specified routing algorithm to determine if the circuit can be established.
 * 
 * 
 * OUTPUT:
 *      total number of virtual circuit requests: 200
		number of successfully routed requests: 100
		percentage of successfully routed request: 50
		number of blocked requests: 100
		percentage of blocked requests: 50
		The average number of hops (i.e. links) consumed per successfully routed virtual circuit.
 		The average source-to-destination cumulative propagation delay per successfully	routed circuit request.

 * @author Sephy
 *
 */
public class RoutingPerformance {
	public static boolean debug = false;
	public static void main(String[] args) throws Exception{
		String protocol = args[0];
		Topology topology = new Topology(args[1]);
		Workload workload = new Workload(args[2]);
		
		if(protocol.equals("SHP")){
			SHP shp = new SHP(topology,workload);	
			shp.run();
		} else if(protocol.equals("SDP")) {
			SDP sdp = new SDP(topology,workload);
			sdp.run();
		} else if (protocol.equals("LLP")) {
			LLP llp = new LLP(topology,workload);
			llp.run();
		} else {
			System.err.println("Wrong protocal name "+ args[0]);
		}
		
	}
}
