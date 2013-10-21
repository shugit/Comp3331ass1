package HelperClass;

import java.util.ArrayList;

import org.junit.Test;

public class MyHashTest {

	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		MyHashMap<Path,ArrayList<Request>> mm = new MyHashMap<Path,ArrayList<Request>>();
		ArrayList<Request> aa = new ArrayList<Request>();
		mm.put(new Path("a","b"), aa);
		ArrayList<Request> bb = mm.get(new Path("a","b"));
		if(mm.containsKey(new Path("a","b"))){
			System.out.println("yeah~~~");
		}		
		if(aa == bb){
			System.out.println("yeah");
		}
		
	}

}
