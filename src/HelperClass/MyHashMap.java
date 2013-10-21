package HelperClass;


import java.util.ArrayList;
import java.util.HashMap;

import MainClass.Topology;

@SuppressWarnings({ "serial", "rawtypes" })
public class MyHashMap<K,V> extends HashMap{
	
	public boolean containsKey(Path key){
		for(Object each : this.keySet()){
			Path theKey = (Path) each;
			if(theKey.getStart().equals(key.getStart()) && theKey.getEnd().equals(key.getEnd())){
				return true;
			}
			if(theKey.getStart().equals(key.getEnd()) && theKey.getEnd().equals(key.getStart())){
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Request> get(Path key){
		for(Object each : this.keySet()){
			Path theKey = (Path) each;
			if(theKey.getStart().equals(key.getStart()) && theKey.getEnd().equals(key.getEnd())){
				return (ArrayList<Request>) super.get(each);
			}
			if(theKey.getStart().equals(key.getEnd()) && theKey.getEnd().equals(key.getStart())){
				return (ArrayList<Request>) super.get(each);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public void myPut(Path key, ArrayList<Request> list){
		int i = Topology.StringToInt(key.getStart(),key.getEnd())[0];
		int j = Topology.StringToInt(key.getStart(),key.getEnd())[1];
		if(i < j){
			super.put(key, list);
		} else {
			super.put(new Path(key.getEnd(),key.getStart()), list);
		}
		return;
	}
	
	
}
