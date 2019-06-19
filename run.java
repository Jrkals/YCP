import java.util.ArrayList;
import java.util.HashMap;

public class run {

	public static void main(String[] args) {
		//String fileName = "/Users/justin/Dropbox/YCP/Seating_Project/small_data_set.csv";
		String fileName = "/Users/justin/Dropbox/YCP/Seating_Project/newest-export.csv";
		CsvReader csvr = new CsvReader(fileName);
		csvr.getItems2();
		ArrayList<Person> people = new ArrayList<Person>();
		people = csvr.getPeopleGoingToGala();
		/*for(Person p: people) {
			System.out.println(p);
		}*/
		
		
		//Read Tables
		String tableFile = "/Users/justin/Dropbox/YCP/Seating_Project/table_mapping.csv";
		TableReader tr = new TableReader(tableFile);
		HashMap<Integer, Integer> map = new HashMap<>();
		map = tr.getMap();
		int numTables = map.size();
		
		Table[] tables = new Table[numTables];
		// generate a list of tables. May do this in file
		for(int i = 0; i < numTables; i++) {
			// use the table map to pick the actual highest priority table
			Table t = new Table(map.get(i));  
			tables[i] = t;
		}
		
		Arranger arr = new Arranger(people, tables);
		arr.arrangeTables();
		arr.printArrangement();

	}

}
