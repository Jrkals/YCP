import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class run {
	final static boolean TESTINGMODE = true;
	final static boolean MAKENEWDATA = false;
	static int numPeople = 500;

	public static void main(String[] args) {
		String fileName = "";
		if(TESTINGMODE) {
			fileName = "/Users/justin/Dropbox/YCP/Seating_Project/fakeData.csv";
			if(MAKENEWDATA) {
				fakeDataCreator fdc = new fakeDataCreator(fileName, numPeople);
				try {
					fdc.writeToFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
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
			t.isVIPTable = true;
		}
		//read in spouse List
		HashMap<String, String> spouseMap = new HashMap<>();
		String spouseFile = "/Users/justin/Dropbox/YCP/Seating_Project/spouseList.csv";
		SpouseReader sr = new SpouseReader(spouseFile);
		spouseMap = sr.getMap();

		Arranger2 arr = new Arranger2(people, tables, VIPTables, spouseMap);
		arr.arrangeTables();
		arr.checkSpousesAndTables();
		// Write output 
		arr.printArrangementToScreen();
		arr.printArrangementToFile("/Users/justin/Dropbox/YCP/Seating_Project/output.csv");
		arr.writeArrangementToDatabase();
		
		// Making Swaps
		System.out.println("Would you like to make any changes? Y/N");
		Scanner scan = new Scanner(System.in);
		String line = scan.nextLine();
		if(line.contains("Y")) {
			SeatSwapper ssw = new SeatSwapper();
			System.out.println("Enter swaps or enter 'done' to quit");
			System.out.print("Use the formula:		 word firstname1 lastname1 word firstname2 lastname2");
			System.out.println("\t(Case does not matter)");
			System.out.println("For example: 'swap Peter schIFf and Tom BRADY'");
			System.out.println("Example 2: 'exchange Sam Adams with Calvin Coolidge'");
			String input = scan.nextLine();
			while(!input.equalsIgnoreCase("done")) {
				if(input.split(" ").length != 6) {
					System.out.println("Error re-type command properly");
					System.out.println("Use the formula swap fn1 ln1 and fn2 ln2");
					System.out.println("For example: 'swap john adams and bill murray'");
				}
				else {
					if(ssw.swap(input)) {
						System.out.println("Success! Enter another swap or 'done'");
					}
					else {
						System.out.println("Swap failed");
					}
				}
				input = scan.nextLine();
			}
		}
		
		scan.close();

	}

}
