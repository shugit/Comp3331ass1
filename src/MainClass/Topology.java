package MainClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Topology {
	private int[][][] array2d = new int[26][26][2];

	private int[][] currentLoad = new int[26][26];
	private ArrayList<String> nodes;
	boolean debug = RoutingPerformance.debug;

	public Topology(String file) throws Exception {
		File myTopoFile = new File(file);
		if (!myTopoFile.exists()) {
			System.err.println("Can't Find " + file);
			return;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(myTopoFile)));
		String line = br.readLine();
		int counta = 0;
		while (line != null) {
			// System.out.println(line);

			String[] words = line.split(" ");
			// System.out.println("Split : " +
			// words[0]+" "+words[1]+" "+words[2]+" "+words[3]);
			/*
			 * char a = words[0].charAt(0); int asciiA = (int) a; char b =
			 * words[1].charAt(0); int asciiB = (int) b;
			 * System.out.println("ascii: "+asciiA+" for "+a);
			 * System.out.println("ascii: "+asciiB+" for "+b);
			 */
			int i = StringToInt(words[0], words[1])[0];
			int j = StringToInt(words[0], words[1])[1];

			array2d[i][j][0] = Integer.parseInt(words[2]);
			array2d[j][i][0] = Integer.parseInt(words[2]);
			array2d[i][j][1] = Integer.parseInt(words[3]);
			array2d[j][i][1] = Integer.parseInt(words[3]);

			counta++;
			line = br.readLine();
		}
		int count = 0;
		for (int i = 0; i < 26; i++) {
			for (int j = 0; j < 26; j++) {
				if (array2d[i][j][0] != 0) {
					char a = (char) (i + 65);
					char b = (char) (j + 65);
					// System.out.println(a+" "+b+" "+array2d[i][j][0]+" ");
					count++;
				}
			}
		}
		// System.out.println("counta "+counta+" count"+count+" ");
		nodes = this.calculateNodes();
	}

	/**
	 * return propagation delay of parameter a and b, exp: A F, return 30
	 * 
	 * @param a
	 *            eg "A"
	 * @param b
	 *            eg "F"
	 * @return eg "330"
	 */
	public int getDelay(String a, String b) {
		int i = StringToInt(a, b)[0];
		int j = StringToInt(a, b)[1];
		if (array2d[i][j][0] == 0) {
			if (debug)
				System.err.println("path Between " + a + " and " + b
						+ " not exists");
			return 0;
		}
		return array2d[i][j][0];
	}

	/**
	 * make smaller char return as 0, lager return as 1
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	static public int[] StringToInt(String a, String b) {
		int[] r = new int[2];
		int i = ((int) a.charAt(0)) - 65;
		int j = ((int) b.charAt(0)) - 65;
		if (i > j) { // I = B, J = A
			r[0] = j;
			r[1] = i;
		}
		r[0] = i;
		r[1] = j;
		return r;
	}

	/**
	 * 
	 * @param a
	 *            eg."A"
	 * @param b
	 *            eg."F"
	 * @return eg."40"
	 */
	public int getCapacity(String a, String b) {
		int i = StringToInt(a, b)[0];
		int j = StringToInt(a, b)[1];
		if (array2d[i][j][1] == 0) {
			if (debug)
				System.err.println("path Between " + a + " " + b
						+ " not exists");
			return 0;
		}
		return array2d[i][j][1];
	}

	public void clearAllLoad() {
		for (int i = 0; i < 26; i++) {
			for (int j = 0; j < 26; j++) {
				currentLoad[i][j] = 0;
			}
		}
	}

	public void setCurrentLoad(int l, String a, String b) {
		int i = StringToInt(a, b)[0];
		int j = StringToInt(a, b)[1];
		if (array2d[i][j][1] != 0) {
			this.currentLoad[i][j] = l;
		} else {
			if (debug)
				System.err.println("path Between " + a + " " + b
						+ " not exists");
		}
	}

	public int getCurrentLoad(String a, String b) {
		int i = StringToInt(a, b)[0];
		int j = StringToInt(a, b)[1];
		if (array2d[i][j][1] == 0) {
			if (debug)
				System.err.println("path Between " + a + " " + b
						+ " not exists");
			return 0;
		}
		return currentLoad[i][j];
	}

	public boolean isOverCapacity(String a, String b) {
		if (getCurrentLoad(a, b) <= getCapacity(a, b)) {
			return true;
		}
		return false;
	}

	public void add1toCurrentLoad(String a, String b) {
		setCurrentLoad(getCurrentLoad(a, b) + 1, a, b);
	}

	public ArrayList<String> getNeibors(String a) {
		ArrayList<String> r = new ArrayList<String>();
		int ascii = ((int) a.charAt(0)) - 65;
		for (int i = 0; i < 26; i++) {
			if (array2d[ascii][i][0] != 0) {
				char cha = (char) (i + 65);
				r.add(cha + "");
			}
		}
		return r;
	}

	public boolean isNeibors(String a, String b) {
		int i = StringToInt(a, b)[0];
		int j = StringToInt(a, b)[1];

		if (array2d[i][j][0] != 0) {
			return true;
		}
		return false;
	}

	private ArrayList<String> calculateNodes() {
		ArrayList<String> r = new ArrayList<String>();
		for (int i = 0; i < 26; i++) {
			for (int j = 0; j < 26; j++) {
				if (array2d[i][j][0] != 0) {
					char cha = (char) (i + 65);
					String s = cha + "";
					if (!r.contains(s)) {
						r.add(s);
					}
				}
			}
		}
		return r;
	}

	public ArrayList<String> getNodes() {
		return nodes;
	}
}
