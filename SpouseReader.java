import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class SpouseReader {
	File file;
	Scanner scan;
	ArrayList<String> lines = new ArrayList<>();
	HashMap<String, String> spouses = new HashMap<>();

	public SpouseReader(String filename) {
		file = new File(filename);
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	} 
	
	ArrayList<String> getRows() {
		skipFirstLine();
		while(scan.hasNext()) {
			String line = scan.nextLine();
			lines.add(line);
		}		
		return lines;
	}
	// return hashmap of spouse names 
	HashMap<String, String> getMap(){
		for(String s: getRows()) {
			String[] sSplit = s.split(","); // splits into 2
			spouses.put(sSplit[0],sSplit[1]);
		}
		return spouses;
	}
	
	private void skipFirstLine() {
		scan.nextLine();
	}
}
