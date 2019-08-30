import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.xml.transform.stream.StreamResult;

public class run {
	final static boolean TESTINGMODE = true;
	final static boolean MAKENEWDATA = false;
	static int numPeople = 20;
	static Table[] VIPTables;
	static Table[] tables;


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
		ArrayList<Person> csvPeople = getPeoplFromCSV(csvr);
		System.out.println("There are "+csvPeople.size()+" people in the csv");
		
		DatabaseReader dbr = new DatabaseReader();
		ArrayList<Person> dbPeoplePeople = dbr.getPeople();
		ArrayList<Person> dbTablePeople = dbr.getPeopleFromTables();
		System.out.println("There are "+dbPeoplePeople.size()+" people in the people table");
		System.out.println("There are "+dbTablePeople.size()+" people in the tables table");
		List<Person> inTablesNotPeople = dbTablePeople.stream().filter(p -> !listContainsName(dbPeoplePeople, p)).collect(Collectors.toList());;

		
		//remove people in db and not csv
		//TODO Test this
		SeatSwapper dbm = new SeatSwapper();
		dbm.deletePeople(inTablesNotPeople);

		//Read Tables
		readTablesFromCSV();

		//read in spouse List
		HashMap<String, String> spouseMap = new HashMap<>();
		String spouseFile = "/Users/justin/Dropbox/YCP/Seating_Project/spouseList.csv";
		SpouseReader sr = new SpouseReader(spouseFile);
		spouseMap = sr.getMap();

		Arranger2 arr = new Arranger2(csvPeople, tables, VIPTables, spouseMap);
		arr.arrangeTables();
		arr.checkSpousesAndTables();
		// Write output 
		arr.printArrangementToScreen();
		arr.printArrangementToFile("/Users/justin/Dropbox/YCP/Seating_Project/output.csv");
		arr.writeArrangementToDatabase();

		// Making Swaps
		makeSwaps();

	}

	private static boolean listContainsName(ArrayList<Person> dbPeoplePeople, Person p) {
		for(Person pe: dbPeoplePeople) {
			if(pe.firstName.equals(p.firstName) && pe.lastName.equals(p.lastName)){
				return true;
			}
		}
		return false;
	}

	private static ArrayList<Person> getPeoplFromCSV(CsvReader csvr) {
		csvr.getItems2();
		ArrayList<Person> people = new ArrayList<Person>();
		people = csvr.getPeopleGoingToGala();
		return people;
	}

	private static void makeSwaps() {
		System.out.println("Would you like to make any changes? Y/N");
		Scanner scan = new Scanner(System.in);
		String line = scan.nextLine();
		if(line.contains("Y")) {
			SeatSwapper ssw = new SeatSwapper();
			System.out.println("Enter swaps or enter 'done' to quit");
			System.out.print("Use the formula:		 word firstname1 lastname1 word firstname2 lastname2");
			System.out.println("Case for the names DOES matter)");
			System.out.println("For example: 'swap Peter Schiff and Tom Brady'");
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

	static void readTablesFromCSV() {
		String tableFile = "/Users/justin/Dropbox/YCP/Seating_Project/table_mapping.csv";
		TableReader tr = new TableReader(tableFile);
		HashMap<Integer, Integer> map = new HashMap<>();
		map = tr.getMap();
		int numTables = map.size();
		int numVIPTables = tr.numVIPS;
		numTables -= numVIPTables;
		//System.out.println("num table"+numTables+" vip: "+numVIPTables);

		tables = new Table[numTables];
		// generate a list of tables. May do this in file
		for(int i = numVIPTables; i < numTables+numVIPTables; i++) {
			// use the table map to pick the actual highest priority table
			Table t = new Table(map.get(i));
			//System.out.println("I is "+i+"table id is"+t.tableID);
			tables[i-numVIPTables] = t;
		}
		VIPTables = new Table[numVIPTables];
		for(int i = 0; i < numVIPTables; i++) {
			// use the table map to pick the actual highest priority table
			Table t = new Table(map.get(i));  
			VIPTables[i] = t;
			t.isVIPTable = true;
		}
	}

}
