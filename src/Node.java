
public class Node {
	private String name;
	private int distance;
	public Node(String s, int d){
		name = s;
		distance = d;
	}
	
	public String getName(){
		return name;
	}
	
	public int getDistance(){
		return distance;
	}
}
