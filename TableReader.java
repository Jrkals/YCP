import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class TableReader {
	File file;
	Scanner scan;
	
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
		HashMap<Integer, Integer> map = new HashMap<>();
		for(String s: getRows()) {
			String[] sSplit = s.split(","); // splits into 2 parts
			map.put(Integer.parseInt(sSplit[0]), Integer.parseInt(sSplit[1]));
		}
		return map;
	}
	
	private void skipFirstLine() {
		scan.nextLine();
	}

}
