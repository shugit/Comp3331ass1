import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class TestCase {

	public void testOn1() throws Exception {
		Topology topo = new Topology("t1");
		Workload work = new Workload("w1");
	}

	@Test
	public void testOn3() throws Exception {
		Topology topo = new Topology("topology.txt");
		Workload work = new Workload("w3");
		SHP s = new SHP(topo, work);
		s.runAll();		
	}
	
	
	
	public void testOn5() throws Exception {
		Topology topo = new Topology("topology.txt");
		Workload work = new Workload("workload.txt");
		SDP s = new SDP(topo, work);
		s.runAll();	
			
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
