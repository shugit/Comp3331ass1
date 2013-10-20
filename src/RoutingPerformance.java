
import java.io.*;
/**
 * argu[3]:SHP/SDP/LLP,TOPOLOGY_FILE,WORKLOAD_FILE
 * TOPOLOGY: A B 10 19 = A��B֮��,10ms�ӳ�,���19��ͬ�������·
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
	public static boolean debug = true;
	public static void main(String[] args) throws Exception{
		String protocal = args[0];
		Topology topology = new Topology(args[1]);
		Workload workload = new Workload(args[2]);
		
		
		
		
	}
}
