import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class run {
	final static boolean TESTINGMODE = true;
	static int numPeople = 500;
	
	public static void main(String[] args) {
		String fileName = "";
		if(TESTINGMODE) {
			fileName = "/Users/justin/Dropbox/YCP/Seating_Project/fakeData.csv";
			
			fakeDataCreator fdc = new fakeDataCreator(fileName, numPeople);
			try {
				fdc.writeToFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			//String fileName = "/Users/justin/Dropbox/YCP/Seating_Project/small_data_set.csv";
			fileName = "/Users/justin/Dropbox/YCP/Seating_Project/newest-export.csv";
		}
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
		int numVIPTables = tr.numVIPS;
		numTables -= numVIPTables;
		//System.out.println("num table"+numTables+" vip: "+numVIPTables);

		Table[] tables = new Table[numTables];
		// generate a list of tables. May do this in file
		for(int i = numVIPTables; i < numTables+numVIPTables; i++) {
			// use the table map to pick the actual highest priority table
			Table t = new Table(map.get(i));
			//System.out.println("I is "+i+"table id is"+t.tableID);
			tables[i-numVIPTables] = t;
		}
		Table[] VIPTables = new Table[numVIPTables];
		for(int i = 0; i < numVIPTables; i++) {
			// use the table map to pick the actual highest priority table
			Table t = new Table(map.get(i));  
			VIPTables[i] = t;
		}

		Arranger2 arr = new Arranger2(people, tables, VIPTables);
		arr.arrangeTables();
		//arr.printArrangement();

	}

}
