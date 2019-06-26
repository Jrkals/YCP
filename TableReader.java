import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class TableReader {
	File file;
	Scanner scan;
	
	HashMap<Integer, Boolean> vipMap = new HashMap<>();
	HashMap<Integer, Integer> map = new HashMap<>();
	
	int numVIPS = 0;


	
	public TableReader(String filename) {
		file = new File(filename);
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	} 
	
	String[] getRows() {
		skipFirstLine();
		int countLine = 0;
		String[] rv = new String[60];
		while(scan.hasNext()) {
			String line = scan.nextLine();
			rv[countLine] = line;
			countLine++;
		}
		if(countLine > 60) {System.out.println("ERROR too many tables");}
		
		return rv;
	}
	
	HashMap<Integer, Integer> getMap(){
		for(String s: getRows()) {
			String[] sSplit = s.split(","); // splits into 2 or 3 parts
			map.put(Integer.parseInt(sSplit[0]), Integer.parseInt(sSplit[1]));
			if(sSplit.length > 2) {
				vipMap.put(Integer.parseInt(sSplit[0]), true);
				numVIPS++;
			}
			else {
				vipMap.put(Integer.parseInt(sSplit[0]), false);
			}
		}
		return map;
	}
	
	HashMap<Integer, Boolean> getVIPMap(){
		return vipMap;
	}
	
	boolean isVIP(String s){
		return s.equalsIgnoreCase("VIP");
	}
	
	private void skipFirstLine() {
		scan.nextLine();
	}
	
	int numVIPS() {
		return numVIPS;
	}

}
