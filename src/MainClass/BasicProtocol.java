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

public abstract class BasicProtocol {
	Topology topology;
	Workload workload;
	boolean debug = RoutingPerformance.debug;
	boolean pastDebug = false;

	/**
	 * 
	 * @param r1
	 *            is main request that we need
	 * @param r2
	 * @return
	 */
	private boolean isTimeInterleaved(Request r1, Request r2) {

		/*
		 * if(r2.getArriveTime() <= r1.getArriveTime() && r2.getEndTime() >
		 * r1.getArriveTime()){ return true; } else if(r2.getArriveTime() >=
		 * r1.getArriveTime() && r2.getArriveTime() < r1.getEndTime()) { return
		 * true; } return false;
		 */

		// r2全都小于r1,或者r2全都大于r1
		if (r2.getArriveTime() < r1.getArriveTime()
				&& r2.getEndTime() < r1.getArriveTime()) {
			return false;
		} else if (r2.getArriveTime() > r1.getEndTime()) {
			return false;
		}
		return true;

	}

	double[][][] requestEndTime;
	double[][][] requestStartTime;

	/**
	 * 
	 * @param a
	 *            path's start
	 * @param b
	 *            path's end
	 * @param requestNumber
	 *            the request number
	 * @param time
	 *            end time(absolute)
	 */
	public void setCapacityEndTime(String a, String b, int requestNumber,
			double time) {
		requestEndTime[((int) a.charAt(0)) - 65][((int) b.charAt(0)) - 65][requestNumber] = time;
	}

	public void setCapacityStartTime(String a, String b, int requestNumber,
			double time) {
		requestStartTime[((int) a.charAt(0)) - 65][((int) b.charAt(0)) - 65][requestNumber] = time;
	}

	public double getCapacityEndTime(String a, String b, int requestNumber) {
		return requestEndTime[((int) a.charAt(0)) - 65][((int) b.charAt(0)) - 65][requestNumber];
	}

	MyHashMap<Path, ArrayList<Request>> pathRequestMap;
	HashMap<Request, ArrayList<String>> requestRouteMap = new HashMap<Request, ArrayList<String>>();

	@SuppressWarnings("unchecked")
	public void run() {
		Map<Request, Boolean> able2Through = new HashMap<Request, Boolean>();
		pathRequestMap = new MyHashMap<Path, ArrayList<Request>>();
		int passed = 0;
		int failed = 0;
		for (Request r : workload.requests) {
			if (debug) {
				System.out.print("FOR ");
				r.print();
			}
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
				System.out.println("	Time neede " + timePassed + " Blocked? "
						+ (isBlocked ? "true" : "false"));
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
		}
		/*
		 * total number of virtual circuit requests: 5884 number of successfully
		 * routed requests: 5839 percentage of successfully routed request:
		 * 99.24 number of blocked requests: 45 percentage of blocked requests:
		 * 0.76 average number of hops per circuit: 4.04 average cumulative
		 * propagation delay per circuit: 248.57
		 */

		printResult(passed, failed);

		if (debug) {
			System.out.println("	in pathRequestMap.keySet()");

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
				//if (start != null && route.get(i) != null) {
					//System.out.println("" + start + route.get(i));
					totalDelay += topology.getDelay(start, route.get(i));

				//}
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
				System.out.println("	there is already " + refinedlist.size()
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

	private Set<String> closeNodes;
	private Set<String> openNodes;
	private Map<String, String> predecessors;
	private Map<String, Integer> distanceMap;

	public void route(String source) {
		closeNodes = new HashSet<String>();
		openNodes = new HashSet<String>();
		distanceMap = new HashMap<String, Integer>();
		predecessors = new HashMap<String, String>();
		distanceMap.put(source, 0);
		openNodes.add(source);
		while (!openNodes.isEmpty()) {
			String node = getMinimum(openNodes);
			if (pastDebug)
				System.out.println("In while loop, node is " + node);
			closeNodes.add(node);
			openNodes.remove(node);
			resolveDijkstra(node);
		}
	}

	private void resolveDijkstra(String node) {
		List<String> adjacentNodes = getNeighbors(node);
		for (String target : adjacentNodes) {
			if (pastDebug)
				System.out.println("In resolveDijkstra target is " + target);
			if (getFromDistanceMap(target) > getFromDistanceMap(node)
					+ getDirectDistance(node, target)) {
				if (pastDebug)
					System.out.println("getFromDistanceMap S>T "
							+ getFromDistanceMap(target)
							+ " is greater than S>D>T "
							+ getFromDistanceMap(node) + "+"
							+ getDirectDistance(node, target));
				distanceMap.put(target, getFromDistanceMap(node)
						+ getDirectDistance(node, target));
				predecessors.put(target, node);
				if (pastDebug)
					System.out.println("predecessors.put " + target + " > "
							+ node);
				openNodes.add(target);
			}
		}

	}

	abstract public int getDirectDistance(String node, String target);

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
				if (getFromDistanceMap(String) < getFromDistanceMap(minimum)) {
					minimum = String;
				}
			}
		}
		return minimum;
	}

	private boolean isClosed(String string) {
		return closeNodes.contains(string);
	}

	private int getFromDistanceMap(String destination) {
		Integer d = distanceMap.get(destination);
		if (d == null) {
			return Integer.MAX_VALUE;
		} else {
			return d;
		}
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
		return path;
	}

}
