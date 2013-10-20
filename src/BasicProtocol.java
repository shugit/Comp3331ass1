import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public abstract class BasicProtocol {
	Topology topology;
	Workload workload;
	boolean debug = RoutingPerformance.debug;
	boolean pastDebug = false;
	
	public void runAll() {

		requestEndTime = new double[26][26][workload.requests.size()];
		requestStartTime = new double[26][26][workload.requests.size()];

		//boolean[] completeRequest = new boolean[workload.requests.size()];
		Map<Request, Boolean> able2Through = new HashMap<Request, Boolean>();

		for (Request r : workload.requests) {
			route(r.getSource());
			ArrayList<String> route = getPath(r.getDestination());
			// ***************** is able to arrive in time limit ? *********/
			String start = route.get(0);
			double timePassed = 0;
			for (int i = 1; i < route.size(); i++) {
				timePassed += topology.getDelay(start, route.get(i));
				start = route.get(i);
			}
			if (timePassed > r.getDuration()) { // not ok
				if (debug) {
					System.out.print("TIME LIMIT N/A  ");
					r.print();
				}
					//completeRequest[workload.requests.indexOf(r)] = false;
					able2Through.put(r, false);
				
			} else { // time is ok
				if (debug) {
					System.out.print("TIME LIMIT OK ");
					r.print();
				}
				//completeRequest[workload.requests.indexOf(r)] = true;
				able2Through.put(r, true);
				// *************** now calculate capacity **************/
				// ************ this need to do for all, end run whole loop again **/
				/*
				for (int i = 1; i < route.size(); i++) {
					//在这里记录了每条capacity,包括[request的开始点和结束点和在workload中的排序]作为key,分别的起始时间和结束时间作为value
					setCapacityEndTime(r.getSource(), r.getDestination(),workload.requests.indexOf(r), r.getEndTime());
					setCapacityStartTime(r.getSource(), r.getDestination(),workload.requests.indexOf(r), r.getArriveTime());
					start = route.get(i);
				}
				 */						
			}
		}
		
		if(debug) System.out.println("******** For Load *********");
		for(Request r : workload.requests){
			//if(completeRequest[workload.requests.indexOf(r)] == true){
			if(able2Through.get(r) == true){
				if(debug) {
					System.out.print("For Every Request Before ");
					r.print();
				}

				route(r.getSource());
				ArrayList<String> route = getPath(r.getDestination());
				//*** 对于每一条通过的path,计算其需要的通路(source->dest)在其开始时间~结束时间的总需求量
				//
				for(int i = 0; i < workload.requests.indexOf(r); i++){
					if(debug) {
						System.out.println("Request before r is No."+i+" / "+workload.requests.indexOf(r));
					}
					Request theRequestBefore = workload.requests.get(i);
					if(able2Through.get(theRequestBefore) == true){ //如果排序在i的request能够成功执行,时间上&&占用上
						if(debug) {
							System.out.println("is need to be assesed");
						}
						route(theRequestBefore.getSource());
						ArrayList<String> routefori = getPath(theRequestBefore.getDestination());
						String pathStart = routefori.get(0);
						for (int j = 1; j < routefori.size(); j++) {
							String pathEnd = routefori.get(j);
							if(isTimeInterleaved(r,theRequestBefore) ) {
								if(debug) {
									System.out.println("time intervaled between"+pathStart +" "+ pathEnd);
								}
								topology.add1toCurrentLoad(pathStart, pathEnd);  
							}	
							pathStart = routefori.get(j);
						}		
					}
				}

				String start = route.get(0);
				for (int i = 1; i < route.size(); i++) {
					String end = route.get(i);
					if(topology.getCurrentLoad(start, end) > topology.getCapacity(start, end)){
						able2Through.put(r, false);
					
					}
					start = route.get(i);
				}
				/*
				if(topology.getCurrentLoad(r.getSource(), r.getDestination()) > topology.getCapacity(r.getSource(), r.getDestination())){
					//如果不通过,则设置它的completeRequest为false,这样后者在loop的时候就不会加载它
					able2Through.put(r, false);
				} else {
					//completeRequest[workload.requests.indexOf(r)] = true;
					able2Through.put(r, true);
				}
				
				*/
				topology.clearAllLoad();



			}
		}

		if(debug){
			System.out.println("assesment done ");
		}
		
		int allRequestCount = workload.requests.size();
		int passed = 0;
		int declined = 0;
		for(Request r : workload.requests){
			if(able2Through.get(r) == true){
				System.out.print("PASSED: ");
				r.print();					
				passed++;
			} else {
				System.out.print("BLOCKED: ");
				r.print();
				declined++;
			}
		}

		System.out.println("PASSED: "+ passed);
		System.out.println("Declined: "+ declined);

	}




	/**
	 * 
	 * @param r1 is main request that we need
	 * @param r2
	 * @return
	 */
	private boolean isTimeInterleaved(Request r1,Request r2){
		if(r2.getArriveTime() <= r1.getArriveTime() ){
			if(r2.getEndTime() >= r1.getArriveTime()){
				return true;
			}
		} else if(r2.getArriveTime() >= r1.getArriveTime() && r2.getArriveTime() < r1.getEndTime()) {
			return true;
		}		
		return false;
	}

	double[][][] requestEndTime;
	double[][][] requestStartTime;


	/**
	 * 
	 * @param a path's start
	 * @param b path's end
	 * @param requestNumber the request number
	 * @param time end time(absolute)
	 */
	private void setCapacityEndTime(String a, String b, int requestNumber,
			double time) {
		requestEndTime[((int) a.charAt(0)) - 65][((int) b.charAt(0)) - 65][requestNumber] = time;
	}

	private void setCapacityStartTime(String a, String b, int requestNumber,
			double time) {
		requestStartTime[((int) a.charAt(0)) - 65][((int) b.charAt(0)) - 65][requestNumber] = time;
	}

	private double getCapacityEndTime(String a, String b, int requestNumber){
		return requestEndTime[((int) a.charAt(0)) - 65][((int) b.charAt(0)) - 65][requestNumber];
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
