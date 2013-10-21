package MainClass;

import java.util.ArrayList;

import org.junit.Test;

import HelperClass.Request;

public class TestCase {

	@Test
	public void testOn7() throws Exception {
		Topology topo = new Topology("topology.txt");
		Workload work = new Workload("workload.txt");
		// SHP s = new SHP(topo, work);
		// s.run();
		LLP s2 = new LLP(topo, work);
		s2.run();
		// PASSED 5749 failed 135
	}
	@Test
	public void testOn5() throws Exception {
		Topology topo = new Topology("topology.txt");
		Workload work = new Workload("workload.txt");
		SDP s = new SDP(topo, work);
		s.run();
		/*
		 * total number of virtual circuit requests: 5884 number of successfully
		 * routed requests: 5310 percentage of successfully routed request:
		 * 90.24 number of blocked requests: 574 percentage of blocked requests:
		 * 9.76 average number of hops per circuit: 3.43 average cumulative
		 * propagation delay per circuit: 141.99
		 */
	}
	@Test
	public void testOn6() throws Exception {
		Topology topo = new Topology("topology.txt");
		Workload work = new Workload("workload.txt");
		SHP s = new SHP(topo, work);
		s.run();
		/*
		 * total number of virtual circuit requests: 5884 number of successfully
		 * routed requests: 5102 percentage of successfully routed request:
		 * 86.71 number of blocked requests: 782 percentage of blocked requests:
		 * 13.29 average number of hops per circuit: 2.71 average cumulative
		 * propagation delay per circuit: 173.35
		 */

	}

	public void testOn4() throws Exception {
		Topology topo = new Topology("topology.txt");
		Workload work = new Workload("w2");
		SDP s = new SDP(topo, work);
		Request re = work.requests.get(28);
		s.route(re.getSource());

		ArrayList<String> r = s.getPath(re.getDestination());
		if (r == null) {
			System.out.println("r is null ");
			return;
		}
		System.out.println("Result: ");
		for (String each : r) {
			System.out.print(" " + each);
		}
		System.out.println("----------------");
		re.print();
	}

	public void testOn2() throws Exception {
		Topology topo = new Topology("topology.txt");
		Workload work = new Workload("w2");
		SHP s = new SHP(topo, work);
		/*
		 * for(Request r: work.requests){ s.route(r); }
		 */
		Request re = work.requests.get(0);
		s.route(re.getSource());

		ArrayList<String> r = s.getPath(re.getDestination());
		if (r == null) {
			System.out.println("r is null ");
			return;
		}
		System.out.println("Result: ");
		for (String each : r) {
			System.out.print(" " + each);
		}
		System.out.println("----------------");
		re.print();
	}

}
