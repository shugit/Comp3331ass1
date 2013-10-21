package MainClass;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import HelperClass.MyHashMap;
import HelperClass.Path;
import HelperClass.Request;

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
public class LLP{
	Topology topology;
	Workload workload;
	boolean pastDebug = false;
	boolean debug = false;
	HashMap<Request, ArrayList<String>> requestRouteMap = new HashMap<Request, ArrayList<String>>();
	public LLP(Topology t, Workload w) {
		topology = t;
		workload = w;
	}

	MyHashMap<Path, ArrayList<Request>> pathRequestMap;

	@SuppressWarnings("unchecked")
	public void run() {
		// Request thisRequest;
		// ArrayList<Request> list;
		// ArrayList<String> thisRoute;
		Map<Request, Boolean> able2Through = new HashMap<Request, Boolean>();
		pathRequestMap = new MyHashMap<Path, ArrayList<Request>>();
		int passed = 0;
		int failed = 0;
		for (Request r : workload.requests) {
			if (debug)
				System.out.print("FOR ");
			thisRequest = r;
			thislist = able2Through.keySet();
			if (debug)
				r.print();
			route(r.getSource());
			ArrayList<String> route = getPath(r.getDestination());
			requestRouteMap.put(r, route);
			// ***************** is able to arrive in time limit ? *********/
			String start = route.get(0);
			double timePassed = 0;
			boolean isBlocked = false;
			for (int i = 1; i < route.size(); i++) {
				timePassed += topology.getDelay(start, route.get(i));
				if (isPathBlocked(start, route.get(i), r)) {
					isBlocked = true;
				}
				start = route.get(i);
			}
			if (debug)
				System.out.println("	in run(): Time neede " + timePassed
						+ " Blocked? " + (isBlocked ? "true" : "false"));
			if (timePassed > r.getDuration() || isBlocked) { // not ok
				if (debug) {
					System.out.print("TIME LIMIT N/A  ");
					r.print();
				}
				able2Through.put(r, false);
				failed++;

			} else { // time is ok & load passed
				// 将path和request的list连接在一起
				String start2 = route.get(0);
				for (int i = 1; i < route.size(); i++) {
					Path newPath = new Path(start2, route.get(i));
					if (!pathRequestMap.containsKey(newPath)) { // 如果之前不存在这个path的占用
						ArrayList<Request> aa = new ArrayList<Request>();
						aa.add(r);
						pathRequestMap.put(newPath, aa);
						if (debug)
							System.out.println("		putting " + start2 + " "
									+ route.get(i) + " into list");

					} else {
						pathRequestMap.get(newPath).add(r);
					}
					start2 = route.get(i);
				}
				able2Through.put(r, true);
			}

			if (able2Through.get(r) != false) {
				if (debug)
					System.out.print(passed + " ABLE TO THROUGH ");
				if (debug)
					r.print();
				passed++;
			}
			thislist = able2Through.keySet();
		}

		// System.out.println("PASSED "+passed+" failed "+failed);

		printResult(passed, failed);

		if (debug) {
			System.out.println("	in pathRequestMap.keySet()");
			System.out.print("	");

			for (Object each : pathRequestMap.keySet()) {
				Path aa = (Path) each;
				System.out.print("" + aa.getStart() + "" + aa.getEnd() + " "
						+ pathRequestMap.get(aa).size() + "	");
			}
		}
	}

	public void printResult(int passed, int failed) {
		DecimalFormat format = new DecimalFormat("#0.00");
		System.out.println("total number of virtual circuit requests: "
				+ (passed + failed));
		System.out.println("number of successfully routed requests: " + passed);
		double d = (double) passed / (passed + failed) * 100;
		System.out.println("percentage of successfully routed request: "
				+ format.format(d));
		System.out.println("number of blocked requests: " + failed);
		d = (double) failed / (passed + failed) * 100;
		System.out.println("percentage of blocked requests: "
				+ format.format(d));
		System.out.println("average number of hops per circuit: "
				+ format.format(averageHops()));
		System.out.println("average cumulative propagation delay per circuit: "
				+ format.format(averageDelay()));

	}

	public double averageDelay() {
		int totalDelay = 0;
		for (Request each : requestRouteMap.keySet()) {
			ArrayList<String> route = requestRouteMap.get(each);
			String start = route.get(0);
			for (int i = 1; i < route.size(); i++) {
				// if(start!=null && route.get(i)!=null){
				// System.out.println(""+start+route.get(i));
				totalDelay += topology.getDelay(start, route.get(i));

				// }
				start = route.get(i);
			}
		}
		return (double) totalDelay / requestRouteMap.keySet().size();
	}

	public double averageHops() {
		int totalHops = 0;
		for (Request each : requestRouteMap.keySet()) {
			totalHops += requestRouteMap.get(each).size() - 1;
		}
		return (double) totalHops / requestRouteMap.keySet().size();
	}

	public boolean isPathBlocked(String a, String b, Request r) {
		if (debug)
			System.out.println("	For Path " + a + " " + b);
		if (pathRequestMap.containsKey(new Path(a, b))) { // 已经存在a到b的request
															// list
			ArrayList<Request> list = pathRequestMap.get(new Path(a, b));
			ArrayList<Request> refinedlist = new ArrayList<Request>();
			for (Request r2 : list) {
				if (isTimeInterleaved(r, r2)) {
					if (debug)
						System.out
								.println("	Time Interleaved between r1 and r2 ");
					refinedlist.add(r2);
				}
			}
			if (debug)
				System.out.println("	there is already" + refinedlist.size()
						+ " requests on this path /"
						+ topology.getCapacity(a, b));

			if (refinedlist.size() >= topology.getCapacity(a, b)) {
				return true;
			} else {
				return false;
			}

		} else {
			if (debug)
				System.out.println("	there is 0 requests on this path");
			return false;
		}
	}

	/********************* depend on both part ****************************/
	private boolean isTimeInterleaved(Request r1, Request r2) {
		if (r2.getArriveTime() < r1.getArriveTime()
				&& r2.getEndTime() < r1.getArriveTime()) {
			return false;
		} else if (r2.getArriveTime() > r1.getEndTime()) {
			return false;
		}
		return true;
	}

	Request thisRequest;
	Set<Request> thislist;

	// ArrayList<String> thisRoute;
	// MyHashMap<Request, ArrayList<String>> requestRouteMap = new
	// MyHashMap<Request, ArrayList<String>>();
	public int CurrentActive(String a, String b) {
		// int[] i = Topology.StringToInt(a, b);
		int count = 0;
		for (Request each : thislist) {
			ArrayList<String> thisRoute = (ArrayList<String>) requestRouteMap
					.get(each);
			// 这里,在thislist里的每个request并没有导出其本身的path
			// System.out.println(isTimeInterleaved(each,thisRequest)?"true":"false");
			// System.out.println(pathInterleaved(thisRoute, new
			// Path(a,b))?"true":"false");
			// each.print();
			if (isTimeInterleaved(each, thisRequest)
					&& pathInterleaved(thisRoute, new Path(a, b))) {
				count++;
			}
		}

		if (debug)
			System.out.println("				In  count is " + count + "/"
					+ thislist.size());
		return count;
	}

	public boolean pathInterleaved(ArrayList<String> route, Path path) {
		String start = route.get(0);
		for (int i = 1; i < route.size(); i++) {
			String end = route.get(i);
			if (start.equals(path.getStart()) && end.equals(path.getEnd())) {
				return true;
			} else if (start.equals(path.getEnd())
					&& end.equals(path.getStart())) {
				return true;
			}
			start = route.get(i);
		}
		return false;
	}

	/******************* Dijkstra ********************/

	private Set<String> closeNodes;
	private Set<String> openNodes;
	private Map<String, String> predecessors;

	/**
	 * from the source to string's list of loads of links
	 */
	private HashMap<String, ArrayList<Double>> loadMap;

	public void route(String source) {
		closeNodes = new HashSet<String>();
		openNodes = new HashSet<String>();
		loadMap = new HashMap<String, ArrayList<Double>>();
		predecessors = new HashMap<String, String>();
		ArrayList<Double> dd = new ArrayList<Double>();
		dd.add(0.0);
		loadMap.put(source, dd);
		openNodes.add(source);
		while (!openNodes.isEmpty()) {
			String node = getMinimum(openNodes);
			if (pastDebug)
				System.out.println("	Route(): In while loop, node is " + node);
			closeNodes.add(node);
			openNodes.remove(node);
			resolveDijkstra(node);
		}
	}

	// 1. return minimal的时候返回的是最少load的link
	// 2. load of path等于是其中load of link的最大值,那么在计算distance的时候要用max(之前的所有load)
	// 3. 把distance都改名为load比较好

	private void resolveDijkstra(String node) {
		List<String> adjacentNodes = getNeighbors(node);
		for (String target : adjacentNodes) {
			if (pastDebug)
				System.out.println("		In resolveDijkstra target is " + target);
			if (getFromLoadMap(target) > Math.max(getFromLoadMap(node), // if ST
																		// >
																		// SNT,
																		// in
																		// LLP,
																		// it's
																		// (LOAD)ST
																		// >
																		// maxLOAD(SNT),则更新为SNT
					getDirectLoad(node, target))) { // 注意这里会返回
													// 无限大>=max(无限大/某值,某值2)
				if (pastDebug) {
					DecimalFormat format = new DecimalFormat("#0.00");
					System.out.println("		getFromDistanceMap S>T "
							+ format.format(getFromLoadMap(target))
							+ " is greater than S>D>T "
							+ format.format(getFromLoadMap(node)) + " or "
							+ getDirectLoad(node, target));
				}
				ArrayList<Double> dd = loadMap.get(target);
				if (dd == null) {
					dd = new ArrayList<Double>();
					loadMap.put(target, dd);
				}
				dd.add(Math.max(getFromLoadMap(node),
						getDirectLoad(node, target)));
				predecessors.put(target, node);
				if (pastDebug)
					System.out.println("		predecessors.put " + target + " > "
							+ node);
				openNodes.add(target);
			}
		}

	}

	public double getDirectLoad(String node, String target) {
		int[] i = Topology.StringToInt(node, target);
		if (i[1] > i[0]) { // 正常顺序,前<后
			double result = (double) CurrentActive(node, target)
					/ topology.getCapacity(node, target);
			if (pastDebug)
				System.out.println("			in getDirectLoad(): " + node + target
						+ " is " + result);
			return result;
		} else {
			double result = (double) CurrentActive(target, node)
					/ topology.getCapacity(target, node);
			if (pastDebug)
				System.out.println("			in getDirectLoad(): " + target + node
						+ " is " + result);
			return result;
		}
	}

	/**
	 * get a list of node that which is adjancent to param but not settled yet
	 * 
	 * @param node
	 * @return
	 */
	private List<String> getNeighbors(String node) {
		List<String> neighbors = new ArrayList<String>();
		for (String neibor : topology.getNeibors(node)) {
			if (!isClosed(neibor)) {
				neighbors.add(neibor);
			}
		}
		return neighbors;
	}

	private String getMinimum(Set<String> Stringes) {
		String minimum = null;
		for (String String : Stringes) {
			if (minimum == null) {
				minimum = String;
			} else {
				if (getFromLoadMap(String) < getFromLoadMap(minimum)) {
					minimum = String;
				}
			}
		}
		return minimum;
	}

	private boolean isClosed(String string) {
		return closeNodes.contains(string);
	}

	/**
	 * 
	 * @param destination
	 * @return the max load of a particular link on the path from source to
	 *         destination
	 */
	private double getFromLoadMap(String destination) {
		ArrayList<Double> d = loadMap.get(destination);
		if (d == null) {
			d = new ArrayList<Double>();
			d.add(0.0);
			loadMap.put(destination, d);
			return Integer.MAX_VALUE;
		}
		return Collections.max(d);
	}

	public ArrayList<String> getPath(String target) {
		ArrayList<String> path = new ArrayList<String>();
		String step = target;
		// check if a path exists
		if (predecessors.get(step) == null) {
			return null;
		}
		path.add(step);
		while (predecessors.get(step) != null) {
			step = predecessors.get(step);
			path.add(step);
		}
		// Put it into the correct order
		Collections.reverse(path);
		if (debug) {
			System.out.print("	Route is: ");

			for (int i = 0; i < path.size(); i++) {
				System.out.print(path.get(i));
			}
			System.out.println();
		}
		return path;
	}

}
