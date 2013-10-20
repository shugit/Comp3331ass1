import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class Workload {
	ArrayList<Request> requests = new ArrayList<Request>();
	boolean debug = RoutingPerformance.debug;
	public Workload(String file) throws Exception {
		File myFile = new File(file);
		if (!myFile.exists()) {
			if(debug) System.err.println("Can't Find " + file);
			return;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(myFile)));
		String line = br.readLine();
		int count = 0;
		while (line != null) {
			String[] words = line.split(" ");
			line = br.readLine();
			requests.add(new Request(words[0], words[1], words[2], words[3]));
			//requests.get(count).print();
			count++;
		}
	}
	
	
}
