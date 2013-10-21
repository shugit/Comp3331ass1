package HelperClass;

import java.text.DecimalFormat;

public class Request {
	private String source;
	private String dest;
	private double arriveTime;
	private double duration;

	public Request(String timea, String a, String b, String timeb) {
		source = a;
		dest = b;
		arriveTime = Double.parseDouble(timea) * 1000;
		duration = Double.parseDouble(timeb) * 1000;
	}

	public void print() {
		DecimalFormat format = new DecimalFormat("#0.00");
		System.out.println("Request: from " + source + "->" + dest
				+ " arrive at " + format.format(arriveTime) + " 	endTime "
				+ format.format(duration + arriveTime));
	}

	public String getSource() {
		return source;
	}

	public String getDestination() {
		return dest;
	}

	public double getArriveTime() {
		return arriveTime;
	}

	public double getDuration() {
		return duration;
	}

	public double getEndTime() {
		return arriveTime + duration;
	}
}
