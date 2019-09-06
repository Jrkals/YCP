import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Arranger2 {
	ArrayList<Person> peopleList = new ArrayList<>();
	ArrayList<Person> VIPList = new ArrayList<>(); 
	
	Table[] tables;
	Table[] vipTables;
	
	ArrayList<Table> tableList = new ArrayList<>();
	ArrayList<Table> vipTableList = new ArrayList<>();
	
	HashMap<Integer, String> chapterMap = new HashMap<>();
	
	// maps from chapter -> persons
	//e.g., Dallas: [John, Mary, Sue], Austin: [Jacob, Juan, Maria]
	HashMap<String, ArrayList<Person>> chapterList = new HashMap<>();
	HashMap<String, ArrayList<Person>> VIPChapterList = new HashMap<>();
	
	HashMap<String, ArrayList<Table>> tableAllocation = new HashMap<>();
	HashMap<String, ArrayList<Table>> VIPTableAllocation = new HashMap<>();

	public Arranger2(ArrayList<Person> ppl, Table[] tbls, Table[] viptables, 
			HashMap<String, String> spsMap) {
		peopleList.addAll(ppl);
		fillVIPList();
		tableList.addAll(Arrays.asList(tbls));
		vipTableList.addAll(Arrays.asList(viptables));
		//System.out.println("VIP table list size "+vipTableList.size());
		//System.out.println("chapter table list size"+tableList.size());
		tables = tbls;
		vipTables = viptables;
		fillChapterList();  // fills chapterList

		fillVIPChapterList();
		addChapters(); // fills chapterMap

		//find spouses
		findSpousesNew(spsMap);
	}
	/*
	 * 
	 */
	private void findSpouses(ArrayList<Person> ppl) {
		int numCouples = 0;
		for(Person p: peopleList) {
			if(p.spouse != null) continue; // don't count twice

			p.findSpouse(peopleList);
			if(p.spouse != null) {
				numCouples++;
			}
		}
		System.out.println("there are "+numCouples+" couples");
	}
	/*
	 * list that contains vip members arranged in their chapters
	 */
	private void fillVIPChapterList() {
		for(Person p: VIPList) {
			// if chapter exists, add the person to it
			if(VIPChapterList.containsKey(p.chapter)) {
				VIPChapterList.get(p.chapter).add(p);
			}
			else { // put in new chapter with that person in it
				ArrayList<Person> lst = new ArrayList<>();
				lst.add(p);
				VIPChapterList.put(p.chapter, lst);
			}
		}

	}

	void arrangeTables() {
		//first alloc the tables that are non modifiable in the db. These have already been set
		allocateNonModifiableTables();
		// second alloc to VIPS
		System.out.println("Doing VIPS...");
		allocateTableToList(VIPChapterList, VIPTableAllocation, vipTableList);
		//printArrangement();
		// then allocate to chapters
		System.out.println("Doing chapters...");
		allocateTableToList(chapterList, tableAllocation, tableList);
		// go through the open seats and give to unknowns and other chapters if still left
		System.out.println("Doing the rest...");
		allocateLeftovers(chapterList, tableAllocation, tableList);
		System.out.println("Done");
	}
	
	/*
	 * some tables in the Database are not changeable 
	 * get these and set them here before really running the automated allocation
	 */
	private void allocateNonModifiableTables() {
		DatabaseReader dr = new DatabaseReader();
		List<Table> allTables = dr.getTables();
		allTables = allTables.stream().filter(t -> !t.isModifiable).collect(Collectors.toList());
		for(Table t: allTables) {
			if(t.isVIPTable) {
				vipTables[findIndexOfTableID(t)] = t;
				/*
				 * the people in this table have names only. Find them in the list of people passed into
				 * this class and then replace them.
				 */
				findPeopleForTable(t); 
			}
			else {
				tables[findIndexOfTableID(t)] = t; 
				findPeopleForTable(t);
			}
		}
		
	}
	/*
	 * returns where in an array of tables the table with the given table id is
	 */
	private int findIndexOfTableID(Table t) {
		if(t.isVIPTable) {
			for (int i = 0; i < vipTables.length; i++) {
				if(vipTables[i].tableID == t.tableID) {
					return i;
				}
			}
		}
		else {
			for(int i = 0; i < tables.length; i++) {
				if(tables[i].tableID == t.tableID) {
					return i;
				}
			}
		}
		System.out.println("couldn't find table "+t.tableID);
		return -1; // should never happen
	}
	/*
	 * matches the names of the people in t with people in peopleList
	 * It replaces the people in t with those from peopleList since the people
	 * in t have only names
	 */
	private void findPeopleForTable(Table t) {
		for(int i = 0; i < 10; i++) {
			if(t.seats[i] != null) {
				Person p = getPersonFromPeopleList(t.seats[i]);
				t.seats[i] = p; // assign this person to the table
				p.table = t;
				// remove this person from the list so they don't get allocated to a table by the 
				// allocator again
				if(p.isVIP) {
					String chapter = p.chapter;
					VIPChapterList.get(p.chapter).remove(p);
					// check to see if you have gotten rid of everyone from that chapter
					if(VIPChapterList.get(chapter).size() == 0) {
						VIPChapterList.remove(chapter);
					}
				}else {
					String chapter = p.chapter;
					//System.out.println(p.chapter);
					//System.out.println(chapterList);
					//System.out.println(chapterList.get(p.chapter));
					//if(chapterList.get(p.chapter).contains(p)) // in case a duplicate was already deleted
					chapterList.get(p.chapter).remove(p);
					// check to see if you have gotten rid of everyone from that chapter
					if(chapterList.get(chapter).size() == 0) {
						chapterList.remove(chapter);
					}
				}
			}
		}
		
	}
	/*
	 * finds a person of the specified name and returns them
	 */
	private Person getPersonFromPeopleList(Person person) {
		System.out.println(person.firstName+" "+person.lastName);
		for(Person p: peopleList) {
			if(p.firstName.equalsIgnoreCase(person.firstName) && p.lastName.equalsIgnoreCase(person.lastName)) {
				return p;
			}
		}
		System.out.println("couldn't find: "+person.firstName+" "+person.lastName);
		return null; //should never do this.
	}
	private void allocateLeftovers(HashMap<String, ArrayList<Person>> list, 
			HashMap<String, ArrayList<Table>> allocation, ArrayList<Table> table_list) {
		while(!list.isEmpty()) {
			String currentChapter = getBiggestChapter(list);
			//System.out.println("biggest chapter is: "+currentChapter);
			int numberOfPeople = list.get(currentChapter).size();
			//System.out.println("num people: "+numberOfPeople);
			int numTablesToAllocate =(numberOfPeople % 10 == 0) ? numberOfPeople/10 : numberOfPeople / 10 + 1; // useful if there are still full tables available
			// if you can allocate these people to new tables
			if(table_list.size() >= numTablesToAllocate) {
				System.out.println("Allocating "+numTablesToAllocate+" table(s) to chapter "+currentChapter);
				allocation.put(currentChapter, new ArrayList<Table>());
				//pop of the number of tables you are allocating and allocate
				for(int i = 0; i < numTablesToAllocate; i++) {
					allocation.get(currentChapter).add(table_list.remove(0));
				}
				// Allocate the seats specifically
				int leftOver = (numberOfPeople / numTablesToAllocate)%10;
				//System.out.println("left over is "+leftOver);
				for(Table t: allocation.get(currentChapter)) {
					// e.g., 21 people over 3 tables. 21 % 3 = 0
					for(int i = 0; i < numberOfPeople/numTablesToAllocate; i++) {
						t.addPerson(list.get(currentChapter).remove(0));
					}
					// e.g., 29 people over 3 tables 29 % 3 = 2
					// add one leftover to this table
					if(leftOver != 0) {
						t.addPerson(list.get(currentChapter).remove(0));
						leftOver--;
					} // if leftOver != 0
				} // loop over tables
				list.remove(currentChapter);
			} // end of if
			else { // they must reuse previously allocated tables
				// get a list of open tables
				ArrayList<Table> openTables = new ArrayList<>();
				for(String chap: allocation.keySet()) {
					if(chap.equals(currentChapter)) continue; // no tables allocated to it yet
					for(Table t: allocation.get(chap)) {
						if(t.numberOfSpots > 0) {
							openTables.add(t);
							//System.out.println("table "+t.tableID+" has open spots");
						}
					} // end of for
				}
				// look in the open tables and fill them in
				for(Table t: openTables) {
					for(int i = t.numberOfSpots; i > 0; i--) {
						//System.out.println("adding to table "+t.tableID);
						if(list.get(currentChapter).size() > 0) {
							t.addPerson(list.get(currentChapter).remove(0));
						}
					}
					//System.out.println("Table "+t.tableID+" now is ");
					//System.out.println(t);
				}
				list.remove(currentChapter);
			} // end of else
		} // end of while

	} // end of method
	/*
	 * takes a list (in form of a hashmap) and allocates tables to them
	 */
	private void allocateTableToList(HashMap<String, ArrayList<Person>> list,
			HashMap<String, ArrayList<Table>> allocation, ArrayList<Table> table_list) {
		while(!list.isEmpty()) {
			String currentChapter = getBiggestChapter(list);
			//System.out.println("biggest chapter is "+currentChapter);
			int numberOfPeople = list.get(currentChapter).size();
			//System.out.println("there are "+numberOfPeople+ " people from "+currentChapter);
			int numTablesToAllocate = numberOfPeople / 10 + 1;
			// should never happen unless there are like 500 VIPS
			if(numTablesToAllocate > table_list.size()) {
				System.out.println("this chapter can't get its own table");
				allocateLeftovers(list, allocation, table_list);
				return;
			}
			else {
				//System.out.println("eAllocating "+numTablesToAllocate+" table(s) to "+currentChapter);
				allocation.put(currentChapter, new ArrayList<Table>());
				//pop of the number of tables you are allocating and allocate
				for(int i = 0; i < numTablesToAllocate; i++) {
					allocation.get(currentChapter).add(table_list.remove(0));
					System.out.println("used table "+allocation.get(currentChapter).get(i).tableID);
				}
				// Allocate the seats specifically
				// with 1 person this is 1. Hence the && on line 152
				int leftOver = numberOfPeople % (numberOfPeople/numTablesToAllocate); 
				//System.out.println("leftover is "+leftOver);
				for(Table t: allocation.get(currentChapter)) {
					//System.out.println("table "+t.tableID);
					// e.g., 21 people over 3 tables. 21 % 3 = 0
					for(int i = 0; i < numberOfPeople/numTablesToAllocate; i++) {
						//System.out.println("adding "+list.get(currentChapter).get(0));
						t.addPerson(list.get(currentChapter).remove(0));
					}
					// e.g., 29 people over 3 tables 29 % 3 = 2
					// add one leftover to this table
					if(leftOver != 0 && numTablesToAllocate > 1) {
						t.addPerson(list.get(currentChapter).remove(0));
						leftOver--;
					} // if leftOver != 0
				} // loop over tables
				list.remove(currentChapter);
			} // end of else
		} // end of while
	} // end of method

	void printArrangementToScreen() {
		OutputWriter ow = new OutputWriter("", vipTables, tables);
		ow.printArrangementToScreen();
	}
	
	void printArrangementToFile(String filename){
		OutputWriter ow = new OutputWriter(filename, vipTables, tables);
		try {
			ow.writeToFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void writeArrangementToDatabase() {
		OutputWriter ow = new OutputWriter("", vipTables, tables);
		ow.writeToDB();
		ow.writePeopleToDB();
	}

	/*
	 * fills the VIP list by searching peopleList
	 * removes vip from normal list
	 */
	private void fillVIPList() {
		for(Person p: peopleList) {
			if(p.isVIP) {
				VIPList.add(p);
			}
		}
		for(Person p: VIPList) {
			peopleList.remove(p);
		}
	}

	/*
	 * fills chapterList hashmap
	 */
	private void fillChapterList() {
		for(Person p: peopleList) {
			// if chapter exists, add the person to it
			if(chapterList.containsKey(p.chapter)) {
				chapterList.get(p.chapter).add(p);
			}
			else { // put in new chapter with that person in it
				ArrayList<Person> lst = new ArrayList<>();
				lst.add(p);
				chapterList.put(p.chapter, lst);
			}
		}
	}

	/*
	 * make an arrayList of the 20 chapters
	 */
	private void addChapters() {
		chapterMap.put(0, "Austin");
		chapterMap.put(1, "Chicago");
		chapterMap.put(2, "Cleveland");
		chapterMap.put(3, "Columbus");
		chapterMap.put(4, "Dallas");
		chapterMap.put(5, "Denver");
		chapterMap.put(6, "Detroit");
		chapterMap.put(7, "Fort Worth");
		chapterMap.put(8, "Houston");
		chapterMap.put(9, "Jacksonville");
		chapterMap.put(10, "Los Angeles");
		chapterMap.put(11, "New Orleans");
		chapterMap.put(12, "Omaha");
		chapterMap.put(13, "Orange County");
		chapterMap.put(14, "Orlando");
		chapterMap.put(15, "Phoenix");
		chapterMap.put(16, "Portland");
		chapterMap.put(17, "San Antonio");
		chapterMap.put(18, "San Diego");
		chapterMap.put(19, "Silicon Valley");
		chapterMap.put(20, "National");
		chapterMap.put(21, "unknown"); // for those without a chapter
	}

	/*
	 * a slow linear search through a hashmap which I am treating
	 * as a priority queue the chapters with the most members get picked first
	 */
	String getBiggestChapter(HashMap<String, ArrayList<Person>> list) {
		String biggestChapter = "unknown";
		int max = -1;
		for(String chapterName: list.keySet()) {
			if(chapterName.equals("unknown")) continue; // these people get seated last
			if(list.get(chapterName).size() > max) {
				max = list.get(chapterName).size();
				biggestChapter = chapterName;
			}
		}
		return biggestChapter;
	}

	public void checkSpousesAndTables(){
		System.out.println("Making sure spouses are at the right spots...");
		for(Table t: tables) {
			//System.out.println(t);
			for(Person p: t.seats) {
				if(p == null) continue; // empty table spot, skip these
				
				if(p.hasSpouse() && p.spouse.table != p.table) {
					//System.out.println(p.firstName+" and "+p.spouse.firstName+ " "+p.lastName+
					//		" need to be together but aren't");
					//System.out.println(p.firstName+" t: "+p.table.tableID+" "+p.spouse.firstName+
					//		" t:"+p.spouse.table.tableID);
					if(t.numberOfSpots> 0) { //simply move into a free spot
						p.spouse.table.removePerson(p.spouse);
						//System.out.println("just removed "+p.spouse.firstName+" "+
						//p.spouse.lastName+ " from table "+p.spouse.table.tableID);
						//System.out.println("Adding "+p.spouse.firstName+" to table "+t.tableID);
						t.addPerson(p.spouse);
					}
					else {
						//swap two people
						//System.out.println("Swapping");
						Person personToSwap = null;
						for(Person q: t.seats) {
							if(!q.hasSpouse()) {
								personToSwap = q;
							}
						}
						// replace spouseless person with p's spouse
						Table oldSpouseTable = p.spouse.table;
						t.replacePerson(personToSwap, p.spouse); 
						// move swapped person to p.spouse's old table
						oldSpouseTable.replacePerson(p.spouse, personToSwap); 
						//System.out.println("swapped "+p.spouse.firstName+" and "+personToSwap.firstName);
					} // else
				} // end of if
			} // end of for people
		} // end of for tables
	} // end of method
	
	/*
	 * goes through map and list and matches spouses
	 */
	private void findSpousesNew(HashMap<String, String> spouseMap) {
		for(String key: spouseMap.keySet()) {
			// find where this person is in the peopleList
			System.out.println(key);
			int indexp1 = findIndexOfPerson(key);
			Person p1 = peopleList.get(indexp1);
			// find where their spouse is in the list
			int spouseindex = findIndexOfPerson(spouseMap.get(key));
			Person p2 = peopleList.get(spouseindex);
			p1.spouse = p2;
			p2.spouse = p1;
			//TODO check if this is necessary with java references???
			// assign them as each other's spouses
			peopleList.set(indexp1, p1); // put updated person back in
			peopleList.set(spouseindex, p2); // put spouse back in
		}
	}
	/*
	 * search peopleList for person of the specified name
	 * string is a full name e.g., "Justin Kalan"
	 */
	private int findIndexOfPerson(String string) {
		for(int i = 0; i < peopleList.size(); i++) {
			// check if first and last name match
			if(peopleList.get(i).firstName.equals(string.split(" ")[0]) &&
					peopleList.get(i).lastName.equals(string.split(" ")[1])){
				return i;
			}
				
		}
		return -1; // should never happen
	}
	
}
