import java.util.Comparator;


public class PQComparator implements Comparator<Node>{

	public int compare(Node o1, Node o2) {
		Integer a = o1.getDistance();
		Integer b = o2.getDistance();
		return a.compareTo(b);
	}


}
